import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

//import groovy.xml.XmlSlurper
import groovy.xml.XmlParser
import groovy.xml.XmlUtil

def eUtilsGet(def function, def params) {
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

        } else {
            errorMsg = "Failed to retrieve data. Response code: ${connection.responseCode}"
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


def entrezSearch(def searchTerm) {
    def function = "esearch.fcgi"

    def params = [
        db: "pubmed",
        term: searchTerm,
        retmode: "xml",
        retmax: "200",
        usehistory: "y"
    ]
    eUtilsGetResult = eUtilsGet(function, params) 
    def eSearchResult = eUtilsGetResult.eUtilsResult   

    count = 1
    //eSearchResult.IdList.Id.each { id -> println("${String.format('%4d', count++)} UID: ${id.text()}") }

    return eUtilsGetResult
}




def entrezSummary(def eUtilsSearchResult) {
    def function = "esummary.fcgi"
    def eSearchResult = eUtilsSearchResult.eUtilsResult   
    def queryKey = eSearchResult.QueryKey.text()
    def webEnv = eSearchResult.WebEnv.text()
    println("?db=pubmed&query_key==${queryKey}&WebEnv=${webEnv}")

    def params = [
        db: "pubmed",
        query_key: queryKey,
        WebEnv: webEnv,
        retmode: "xml",
        retmax: "200",
    ]
    eUtilsGetResult = eUtilsGet(function, params) 
    def eSummaryResult = eUtilsGetResult.eUtilsResult  

    //def prettyXml = XmlUtil.serialize(eSummaryResult)
    //print(prettyXml) 

    def docSums = eSummaryResult.DocSum
    
    counter = 1
    docSums.each { docSum ->
        def authorList = docSum.Item.find { it.@Name == 'AuthorList' }?.Item.findAll { it.@Name == 'Author' }?.collect { it.text() }
        def title = docSum.Item.find { it.@Name == 'Title' }.text()
        def pubDate = docSum.Item.find { it.@Name == 'PubDate' }.text()
        def source = docSum.Item.find { it.@Name == 'Source' }.text()
        def volume = docSum.Item.find { it.@Name == 'Volume' }.text()
        def issue = docSum.Item.find { it.@Name == 'Issue' }.text()
        def pages = docSum.Item.find { it.@Name == 'Pages' }.text()


        if (authorList) {
            def concatenatedAuthors = authorList.join(', ')
            println "${counter++}:ID ${docSum.Id.text()}: $concatenatedAuthors"
        }

        println("${title} ${source}. ${pubDate};${volume}(${issue}):${pages}")

        def articleIds = docSum.Item.find { it.@Name == 'ArticleIds' }
        if (articleIds) {
            def articleIdsString = articleIds.Item.collect { "${it.@Name}: ${it.text()}" }.join(', ')
            println "$articleIdsString"
        }

        println()
    }

    
    return eUtilsGetResult 
}


// Call the function with the search term
def search_term = """((walker ak[Author]) NOT (adam k walker[Author]) NOT (Angela K Walker) NOT (Allison K Walker) 
                       NOT (Alexandra K Walker) NOT (Alexis K Walker) NOT (Aaron K Walker) NOT (Anaesthesia[jour]) 
                       NOT (24335193[UID]) NOT (8531707[UID]) NOT (22267235[UID]) NOT (25264390[UID]) NOT (24777035[UID]) 
                       NOT (17896077[UID]) NOT (24751964[UID]) NOT (21907243[UID]) NOT (21438770[UID]) NOT (11332761[UID]) 
                       NOT (20666652[UID]) NOT (17337586[UID]) NOT (9921134[UID]) NOT (10419626[UID]) NOT (10548814[UID]) 
                       NOT (8700510[UID])  NOT (10631665[UID])) OR (21124729[UID])"""
search_term = "(21124729[UID]) OR (37980357[UID])" 
eUtilsSearchResult   = entrezSearch(search_term)
eUtilsSummaryResults = entrezSummary(eUtilsSearchResult)

