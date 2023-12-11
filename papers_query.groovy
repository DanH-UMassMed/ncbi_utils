
import EntrezEUtils
import QueryConsts

searchTerm = QueryConsts.JOURNALS + " AND " + QueryConsts.PUBLISHED_W_IN_5_YRS + " AND "

def conferenceTopics = ""
//conferenceTopics = """("Lipid peroxidation"[All Fields] AND ferroptosis[All Fields])""" //107 Results
conferenceTopics = """("Lipid asymmetry"[All Fields])""" //17 Results
//conferenceTopics = """("Membrane contact"[All Fields] AND "lipid"[All Fields])""" //80

//conferenceTopics = """("Lipid function"[All Fields] AND "organelles"[All Fields])"""

searchTerm += conferenceTopics 

Logger.delete_log_file()

eUtilsSearchResult   = EntrezEUtils.entrezSearch(searchTerm: searchTerm)
//eUtilsSummaryResult = EntrezEUtils.entrezSummary(eUtilsSearchResult: eUtilsSearchResult)
eUtilsFetchResult = EntrezEUtils.entrezFetch(eUtilsSearchResult: eUtilsSearchResult, retmax: "2")
print("Search Terms:${conferenceTopics}\n\n")
//EntrezEUtils.author_report(eUtilsSummaryResult)
