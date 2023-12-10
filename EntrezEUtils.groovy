import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

import groovy.xml.XmlParser
import groovy.xml.XmlUtil
class EntrezEUtils {

    static def eUtilsGet(def function, def params) {
        def logger = new Logger() // Creates or opens log.txt
        def base_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/${function}"

        def eUtilsResult = null
        def eUtilsError = null

        try {
            def query = params.collect { k, v -> "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v, 'UTF-8')}" }.join('&')
            def url = new URL("${base_url}?${query}")

            def connection = url.openConnection()
            connection.setRequestMethod("GET")

            if (connection.responseCode == 200) {
                def inputStream = connection.inputStream
                //println("Got a response")

                def responseText = new Scanner(inputStream).useDelimiter("\\A").next()

                def parser = new XmlParser()
                parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                eUtilsResult = parser.parseText(responseText)

                def prettyXml = XmlUtil.serialize(eUtilsResult)
                logger.log(prettyXml)
                logger.log('='*50)
            } else {
                def errorMsg = "Failed to retrieve data. Response code: ${connection.responseCode}"
                println(errorMsg)
                eUtilsError = errorMsg
            }

        } catch (Exception ex) {
            println("An error occurred: ${ex.message}")
            ex.printStackTrace()
            eUtilsError = ex.message
        }

        return ["function": function, "eUtilsResult": eUtilsResult, "eUtilsError": eUtilsError]

    }

    static def entrezSearch(def searchTerm, db = "pubmed") {
        def function = "esearch.fcgi"

        def params = [
            db: db,
            term: searchTerm,
            retmode: "xml",
            retmax: "500",
            usehistory: "y"
        ]
        def eUtilsGetResult = eUtilsGet(function, params) 
        def eSearchResult = eUtilsGetResult.eUtilsResult   

        //count = 1
        //eSearchResult.IdList.Id.each { id -> println("${String.format('%4d', count++)} UID: ${id.text()}") }

        return eUtilsGetResult
    }

    static def entrezSummary(def eUtilsSearchResult, db = "pubmed") {
        return entrezGetDetails(def eUtilsSearchResult, db, "esummary.fcgi")

    static def entrezFetch(def eUtilsSearchResult, db = "pubmed") {
        return entrezGetDetails(def eUtilsSearchResult, db, "efetch.fcgi")
    }

    static def entrezGetDetails(def eUtilsSearchResult, db, function) {
        def eSearchResult = eUtilsSearchResult.eUtilsResult   
        def queryKey = eSearchResult.QueryKey.text()
        def webEnv = eSearchResult.WebEnv.text()
        //println("?db=pubmed&query_key==${queryKey}&WebEnv=${webEnv}")

        def params = [
            db: db,
            query_key: queryKey,
            WebEnv: webEnv,
            retmode: "xml",
            retmax: "500",
        ]
        def eUtilsGetResult = eUtilsGet(function, params)     
        return eUtilsGetResult 


    static def author_report(def eUtilsSummaryResult) {
        def eSummaryResult = eUtilsSummaryResult.eUtilsResult  

        //def prettyXml = XmlUtil.serialize(eSummaryResult)
        //print(prettyXml) 

        def docSums = eSummaryResult.DocSum
        
        def counter = 1
        docSums.each { docSum ->
            def authorList = docSum.Item.find { it.@Name == 'AuthorList' }?.Item.findAll { it.@Name == 'Author' }?.collect { it.text() }
            def title = docSum.Item.find { it.@Name == 'Title' }.text()
            def pubDate = docSum.Item.find { it.@Name == 'PubDate' }.text()
            def source = docSum.Item.find { it.@Name == 'Source' }.text()
            def volume = docSum.Item.find { it.@Name == 'Volume' }.text()
            def issue = docSum.Item.find { it.@Name == 'Issue' }.text()
            def pages = docSum.Item.find { it.@Name == 'Pages' }.text()

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

