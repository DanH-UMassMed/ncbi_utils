import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

import groovy.xml.XmlSlurper

def search_term = "Walker AK[Author]"
def base_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi"

def params = [
    db: "pubmed",
    term: search_term,
    retmode: "json"
]

def query = params.collect { k, v -> "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v, 'UTF-8')}" }.join('&')
def url = new URL("${base_url}?${query}")

def connection = url.openConnection()
connection.setRequestMethod("GET")

if (connection.responseCode == 200) {
    def inputStream = connection.inputStream
    def responseText = new Scanner(inputStream).useDelimiter("\\A").next()

    def data = new XmlSlurper().parseText(responseText)

    if (data.esearchresult.count.toInteger() > 0) {
        def papers = data.esearchresult.idlist

        def efetch_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
        def efetchParams = [
            db: "pubmed",
            id: papers.join(","),
            retmode: "xml",
            rettype: "abstract"
        ]

        def efetchQuery = efetchParams.collect { k, v -> "${URLEncoder.encode(k, 'UTF-8')}=${URLEncoder.encode(v, 'UTF-8')}" }.join('&')
        def efetchUrl = new URL("${efetch_url}?${efetchQuery}")

        def efetchConnection = efetchUrl.openConnection()
        efetchConnection.setRequestMethod("GET")

        if (efetchConnection.responseCode == 200) {
            def efetchInputStream = efetchConnection.inputStream
            def efetchResponseText = new Scanner(efetchInputStream).useDelimiter("\\A").next()

            // Process XML data using XmlSlurper
            def parsedXml = new XmlSlurper().parseText(efetchResponseText)

            parsedXml.PubmedArticle.each { article ->
                def articleTitle = article.ArticleTitle.text().trim()
                def authors = article.Author
                def authorNames = authors.collect { "${it.LastName.text()} ${it.Initials.text()}" }.join(", ")

                println("Title: $articleTitle")
                println("Authors: $authorNames")
                println("------------------------------------")
            }
        } else {
            println("Fetching paper titles failed with status code: ${efetchConnection.responseCode}")
        }
    } else {
        println("No papers found for the given author.")
    }
} else {
    println("Request failed with status code: ${connection.responseCode}")
}
