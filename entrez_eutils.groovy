import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

//import groovy.xml.XmlSlurper
import groovy.xml.XmlParser
import groovy.xml.XmlUtil

import EntrezEUtils


def entrezSearch(def searchTerm) {
    def function = "esearch.fcgi"

    def params = [
        db: "pubmed",
        term: searchTerm,
        retmode: "xml",
        retmax: "500",
        usehistory: "y"
    ]
    eUtilsGetResult = EntrezEUtils.eUtilsGet(function, params) 
    def eSearchResult = eUtilsGetResult.eUtilsResult   

    //count = 1
    //eSearchResult.IdList.Id.each { id -> println("${String.format('%4d', count++)} UID: ${id.text()}") }

    return eUtilsGetResult
}

def entrezSummary(def eUtilsSearchResult) {
    def function = "esummary.fcgi"
    def eSearchResult = eUtilsSearchResult.eUtilsResult   
    def queryKey = eSearchResult.QueryKey.text()
    def webEnv = eSearchResult.WebEnv.text()
    //println("?db=pubmed&query_key==${queryKey}&WebEnv=${webEnv}")

    def params = [
        db: "pubmed",
        query_key: queryKey,
        WebEnv: webEnv,
        retmode: "xml",
        retmax: "500",
    ]
    eUtilsGetResult = EntrezEUtils.eUtilsGet(function, params)     
    return eUtilsGetResult 
}

def author_report(def eUtilsSummaryResult) {
    def eSummaryResult = eUtilsSummaryResult.eUtilsResult  

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

def download_links(def eUtilsSummaryResult) {
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

// Call the function with the search term
def amys_papers = """((walker ak[Author]) NOT (adam k walker[Author]) NOT (Angela K Walker) NOT (Allison K Walker) 
                       NOT (Alexandra K Walker) NOT (Alexis K Walker) NOT (Aaron K Walker) NOT (Anaesthesia[jour]) 
                       NOT (24335193[UID]) NOT (8531707[UID]) NOT (22267235[UID]) NOT (25264390[UID]) NOT (24777035[UID]) 
                       NOT (17896077[UID]) NOT (24751964[UID]) NOT (21907243[UID]) NOT (21438770[UID]) NOT (11332761[UID]) 
                       NOT (20666652[UID]) NOT (17337586[UID]) NOT (9921134[UID]) NOT (10419626[UID]) NOT (10548814[UID]) 
                       NOT (8700510[UID])  NOT (10631665[UID])) OR (21124729[UID])"""

def publishedRange = "(2018[Date - Publication] : 2024[Date - Publication])"
def publishedAIn5Yrs = "(y_5[Filter])"

def journals ="""
(Cell[Journal] OR
Cell Metabolism[Journal] OR
Molecular Cell[Journal] OR
Cell Reports[Journal] OR
Cell Systems[Journal] OR
Current Biology[Journal] OR
Developmental Cell[Journal] OR
Structure[Journal] OR
Nature[Journal] OR
Nature Cell Biology[Journal] OR
Nature Metabolism[Journal] OR
Nature Aging[Journal] OR
Nature Communications[Journal] OR
Science[Journal] OR
Science Signaling[Journal] OR
Science Advances[Journal] OR
PLoS Biology[Journal] OR
PLoS Genetics[Journal] OR
Nature Reviews Molecular Cell Biology[Journal] OR
Nature Reviews Genetics[Journal] OR
Trends in Endocrinology and Metabolism[Journal] OR
Trends in Genetics[Journal] OR
Trends in Biochemical Sciences[Journal] OR
Trends in Cell Biology[Journal] OR
Annual Review of Genetics[Journal] OR
Annual Review of Biochemistry[Journal] OR
Annual Review of Cell and Developmental Biology[Journal] OR
Annual Review of Nutrition[Journal] OR
Journal of Lipid Research[Journal] OR
Journal of Cell Biology[Journal] OR
Molecular and Cellular Biology[Journal] OR
Journal of Biological Chemistry[Journal] OR
PNAS[Journal] OR
eLife[Journal] OR
The EMBO Journal[Journal] OR
Nucleic Acids Research[Journal] OR
Journal of Cell Science[Journal] OR
Developmental Biology[Journal] OR
Genes and Development[Journal])
"""

def conferenceTopics = ""
//conferenceTopics = """("Lipid asymmetry"[All Fields] AND "membranes"[All Fields])"""
//conferenceTopics = """("Membrane contact"[All Fields] AND "sites"[All Fields])"""
//conferenceTopics = """("Lipid function"[All Fields] AND "organelles"[All Fields])"""

conferenceTopics = """("lipid peroxidation"[All Fields] AND "ferroptosis"[All Dields]"""

search_term = conferenceTopics + "AND" + journals + "AND" + publishedRange
eUtilsSearchResult   = entrezSearch(search_term)
eUtilsSummaryResult = entrezSummary(eUtilsSearchResult)
print("Search Terms:${conferenceTopics}\n\n")
author_report(eUtilsSummaryResult)
