import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

//import groovy.xml.XmlSlurper
import groovy.xml.XmlParser
import groovy.xml.XmlUtil
class EntrezEUtils {

    static def eUtilsGet(def function, def params) {
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

}

