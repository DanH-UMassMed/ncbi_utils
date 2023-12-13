package med_chat.ncbi

import med_chat.utils.Logger
import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

import groovy.xml.XmlParser
import groovy.xml.XmlUtil
class EntrezEUtils {

    static def eUtilsGet(def function, def params, dumpResult = false) {
        def logger = new Logger() // Creates or opens log.txt
        def base_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/${function}"

        def max_retries = 3
        def retry = 0
        def done = false

        def eUtilsResult = null
        def eUtilsError = null

        // Closure to Handle Retries and Print Error Message
        def handle_error = { errorMsg ->
            System.err.println(errorMsg)
            if(retry >= max_retries) {
                done = true
                eUtilsError = errorMsg
            } 
        }

        while (!done) {
            try {
                def query = params.collect { k, v -> "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v, 'UTF-8')}" }.join('&')
                def url = new URL("${base_url}?${query}")
                //println(url)
                    def connection = url.openConnection()
                    connection.setRequestMethod("GET")
                    if (connection.responseCode == 429) {
                        handle_error("eUtilsGet: Request limiter hit. [Retry: ${++retry} ] code: ${connection.responseCode}")
                        Thread.sleep(2000)
                    } else if (connection.responseCode == 200) {
                        done = true
                        def inputStream = connection.inputStream

                        def responseText = new Scanner(inputStream).useDelimiter("\\A").next()

                        def parser = new XmlParser()
                        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                        eUtilsResult = parser.parseText(responseText)

                        def prettyXml = XmlUtil.serialize(eUtilsResult)
                        logger.log(prettyXml)
                        logger.log('='*50)

                        if(dumpResult) {
                            System.err.println("XML Dump")
                            System.err.println(prettyXml)
                        }

                    } else {
                        handle_error("eUtilsGet: Failed to retrieve data. [Retry: ${++retry} ] Response code: ${connection.responseCode}")
                    }
            } catch (Exception ex) {
                handle_error("eUtilsGet: Check if you have a connection!! [Retry: ${++retry}] Response msg: ${ex.message}\n")
                //ex.printStackTrace()
            }
        }

        return ["function": function, "eUtilsResult": eUtilsResult, "eUtilsError": eUtilsError]

    }

    static def entrezSearch(Map methodParams) {
        def db         = methodParams.db          ?: "pubmed"
        def searchTerm = methodParams.searchTerm
        def retmax     = methodParams.retmax      ?: "200"
        def function   = "esearch.fcgi"
        def dumpResult = methodParams.dumpResult  ?: false

        if (searchTerm == null) {
            throw new Exception("function cannot be null!")
        }
        //println("searchTerm=${searchTerm}, db=${db}, dumpResult=${dumpResult} function=${function}")
        //def eUtilsGetResult = null

        def params = [
            db: db,
            term: searchTerm,
            retmode: "xml",
            retmax: retmax,
            usehistory: "y"
        ]

        def eUtilsGetResult = eUtilsGet(function, params, dumpResult) 
        def eSearchResult = eUtilsGetResult.eUtilsResult   


        return eUtilsGetResult
    }

    static def entrezSummary(Map methodParams) {
        methodParams['function'] = "esummary.fcgi" 
        return entrezGetDetails(methodParams)
    }

    static def entrezFetch(Map methodParams) {
        methodParams['function'] = "efetch.fcgi" 
        return entrezGetDetails(methodParams)
    }

    static def entrezGetDetails(Map methodParams) {
        def eUtilsSearch = methodParams.eUtilsSearchResult ?: ""
        def db           = methodParams.db                 ?: "pubmed"
        def retmax       = methodParams.retmax             ?: "200"
        def restart      = methodParams.restart            ?: "0"
        def dumpResult   = methodParams.dumpResult         ?: false
        def function     = methodParams.function

        if (function == null) {
            throw new Exception("function cannot be null!")
        }

        def eSearchResult = eUtilsSearch.eUtilsResult   
        def queryKey = eSearchResult.QueryKey.text()
        def webEnv = eSearchResult.WebEnv.text()

        def params = [
            db: db,
            query_key: queryKey,
            WebEnv: webEnv,
            retmode: "xml",
            retmax: retmax,
            retstart: restart
        ]

        def eUtilsGetResult = eUtilsGet(function, params, dumpResult)    

        return eUtilsGetResult 
    }

    static def author_report(def eUtilsSummaryResult) {
        def eSummaryResult = eUtilsSummaryResult.eUtilsResult  

        //def prettyXml = XmlUtil.serialize(eSummaryResult)
        //print(prettyXml) 

        def docSums = eSummaryResult.DocSum
        
        def counter = 1
        docSums.each { docSum ->
            def authorList = docSum.Item.find { it.@Name == 'AuthorList' }?.Item.findAll { it.@Name == 'Author' }?.collect { it.text() }
            def title   = docSum.Item.find { it.@Name == 'Title'   }.text()
            def pubDate = docSum.Item.find { it.@Name == 'PubDate' }.text()
            def source  = docSum.Item.find { it.@Name == 'Source'  }.text()
            def volume  = docSum.Item.find { it.@Name == 'Volume'  }.text()
            def issue   = docSum.Item.find { it.@Name == 'Issue'   }.text()
            def pages   = docSum.Item.find { it.@Name == 'Pages'   }.text()

            println("${counter++}: ${title} ${source}. ${pubDate};${volume}(${issue}):${pages}")

            if (authorList) {
                def concatenatedAuthors = authorList.join(', ')
                println "$concatenatedAuthors"
            }


            def articleIds = docSum.Item.find { it.@Name == 'ArticleIds' }
            if (articleIds) {
                def articleIdsString = articleIds.Item.collect { "${it.@Name}: ${it.text()}" }.join(', ')
                println "$articleIdsString"
            }
            println "https://pubmed.ncbi.nlm.nih.gov/${docSum.Id.text()}"

            println()
        }
    }

    static def download_links(def eUtilsSummaryResult) {
        def eSummaryResult = eUtilsSummaryResult.eUtilsResult   
        //def prettyXml = XmlUtil.serialize(eSummaryResult)
        //print(prettyXml) 
        println("<html>")
        println("<h1>Download links</h1>")
        println("<ul>")
        def docSums = eSummaryResult.DocSum

        docSums.each { docSum ->
                println "<li><a href=https://pubmed.ncbi.nlm.nih.gov/${docSum.Id.text()}>${docSum.Id.text()}</a></li>"
            }

        println("</ul>")
        println("</html>")

    }

}

