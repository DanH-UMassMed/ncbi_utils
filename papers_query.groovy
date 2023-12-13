
import med_chat.ncbi.EntrezEUtils
import med_chat.ncbi.QueryConsts
import med_chat.impact_factor.ImpactFactorDB
import med_chat.utils.Logger
import med_chat.ncbi.ArticlesManager

import java.util.LinkedList

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
        writer.write("pmid,issn,eissn,pub_year,pub_abbr,title\n")
        def counter = 1
        articles.each { article ->
            def pmid      = article.MedlineCitation.PMID.text()
            def paper     = article.MedlineCitation.Article 
            def issn      = paper.Journal.ISSN.find { it.@IssnType == 'Electronic' }?.text() ?: ""
            def eissn     = paper.Journal.ISSN.find { it.@IssnType == 'Print' }?.text() ?: ""
            def pubYear   = paper.Journal.JournalIssue.PubDate.Year.text()
            def pubAbbr   = paper.Journal.ISOAbbreviation.text()
            def title     = paper.ArticleTitle.text()
            def aAbstract = paper.Abstract.AbstractText.text()
            def impacttFactorDB = ImpactFactorDB.getInstance()
            def impactFactor = impacttFactorDB.getImpactFactor(issn) ?: impacttFactorDB.getImpactFactor(eissn) 

            def outputLine = "${pmid},${issn},${eissn},${pubYear},\"${pubAbbr}\",\"${title}\",${impactFactor}\n"
            writer.write(outputLine) 
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
articlesManager = new ArticlesManager()
articlesManager.articles_query(searchTerm)
articlesManager.toCSV()

//eUtilsSearchResult   = EntrezEUtils.entrezSearch(searchTerm: searchTerm)
//eUtilsSummaryResult = EntrezEUtils.entrezSummary(eUtilsSearchResult: eUtilsSearchResult)
//eUtilsFetchResult = EntrezEUtils.entrezFetch(eUtilsSearchResult: eUtilsSearchResult, retmax: "200")

//paper_details(eUtilsFetchResult,"./output/papers.csv")
//author_list(eUtilsFetchResult,  "authors.csv")
//EntrezEUtils.author_report(eUtilsSummaryResult)
