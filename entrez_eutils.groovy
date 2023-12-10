import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

import groovy.xml.XmlParser
import groovy.xml.XmlUtil

import EntrezEUtils
import QueryConsts

search_term = QueryConsts.JOURNALS + " AND " + QueryConsts.PUBLISHED_W_IN_5_YRS + " AND "


def conferenceTopics = ""
conferenceTopics = """("Lipid asymmetry"[All Fields])""" //23 Results
conferenceTopics = """("Membrane contact"[All Fields] AND "lipid"[All Fields])""" //87
//conferenceTopics = """("Lipid peroxidation"[All Fields] AND ferroptosis[All Fields])""" //116 Results
//conferenceTopics = """("Lipid function"[All Fields] AND "organelles"[All Fields])"""

search_term += conferenceTopics 

eUtilsSearchResult   = EntrezEUtils.entrezSearch(search_term)
eUtilsSummaryResult = EntrezEUtils.entrezSummary(eUtilsSearchResult)
//eUtilsFetchResult = EntrezEUtils.entrezFetch(eUtilsSearchResult)
print("Search Terms:${conferenceTopics}\n\n")
EntrezEUtils.author_report(eUtilsSummaryResult)
