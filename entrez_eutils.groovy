
import EntrezEUtils
import QueryConsts

searchTerm = QueryConsts.JOURNALS + " AND " + QueryConsts.PUBLISHED_W_IN_5_YRS + " AND "

def conferenceTopics = ""
conferenceTopics = """("Lipid asymmetry"[All Fields])""" //23 Results
conferenceTopics = """("Membrane contact"[All Fields] AND "lipid"[All Fields])""" //87
//conferenceTopics = """("Lipid peroxidation"[All Fields] AND ferroptosis[All Fields])""" //116 Results
//conferenceTopics = """("Lipid function"[All Fields] AND "organelles"[All Fields])"""

searchTerm += conferenceTopics 

println("1")
eUtilsSearchResult   = EntrezEUtils.entrezSearch(searchTerm: searchTerm)
println("2")
eUtilsSummaryResult = EntrezEUtils.entrezSummary(eUtilsSearchResult: eUtilsSearchResult)
println("3")
print("Search Terms:${conferenceTopics}\n\n")
EntrezEUtils.author_report(eUtilsSummaryResult)
