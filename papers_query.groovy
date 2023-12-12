
import EntrezEUtils
import QueryConsts


def author_list(def eUtilsFetchResult, String fileName) {
    def eFetchResult = eUtilsFetchResult.eUtilsResult  
    def articles = eFetchResult.PubmedArticle
    
    def outputFile = new File(fileName)
    outputFile.withWriter { writer ->
        writer.write("pmid,lastName,firstName,initials,affiliation,orcid,authorPos\n")
        def counter = 1
        articles.each { article ->
            def pmid = article.MedlineCitation.PMID.text()
            def authorList = article.MedlineCitation.Article.AuthorList.Author.findAll()

            authorList.eachWithIndex { author, i ->
                def lastName = author.LastName.text() ?: ""
                def firstName = author.ForeName.text() ?: ""
                def initials = author.Initials.text() ?: ""
                def orcid = author.Identifier.find { it.@Source == 'ORCID' }?.text() ?: ""
                def authorPos = (i == authorList.size() - 1) ? "L" : (i + 1).toString()
                def affiliation = author.AffiliationInfo.Affiliation.text() ?: ""

                def outputLine = "${pmid},${lastName},${firstName},${initials},\"${affiliation}\",${orcid},${authorPos}\n"
                writer.write(outputLine)
            }
        }
    }
}

def paper_details(def eUtilsFetchResult, String fileName) {
    def eFetchResult = eUtilsFetchResult.eUtilsResult  
    def articles = eFetchResult.PubmedArticle
    
    def outputFile = new File(fileName)
    outputFile.withWriterAppend { writer ->
        writer.write("pmid,lastName,firstName,initials,affiliation,orcid,authorPos\n")
        def counter = 1
        articles.each { article ->
            def pmid = article.MedlineCitation.PMID.text()
            def authorList = article.MedlineCitation.Article.AuthorList.Author.findAll()

            authorList.eachWithIndex { author, i ->
                def lastName = author.LastName.text() ?: ""
                def firstName = author.ForeName.text() ?: ""
                def initials = author.Initials.text() ?: ""
                def orcid = author.Identifier.find { it.@Source == 'ORCID' }?.text() ?: ""
                def authorPos = (i == authorList.size() - 1) ? "L" : (i + 1).toString()
                def affiliation = author.AffiliationInfo.Affiliation.text() ?: ""

                def outputLine = "${pmid},${lastName},${firstName},${initials},\"${affiliation}\",${orcid},${authorPos}\n"
                writer << outputLine
            }
        }
    }
}


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
fileName="author.csv"
author_list(eUtilsFetchResult, fileName)
//EntrezEUtils.author_report(eUtilsSummaryResult)
