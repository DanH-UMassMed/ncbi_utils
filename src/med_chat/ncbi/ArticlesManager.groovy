package med_chat.ncbi

import med_chat.impact_factor.ImpactFactorDB
        
class ArticlesManager {
    def articles = [] as List<Article>
    def authors = [] as List<Author>
    def toCSVItems = [authors, articles]

    ArticlesManager(List<Article> initialArticles = []) {
        articles.addAll(initialArticles)
        // Add a sort method to the articles list
        articles.metaClass.sort = {
            println "sorting"
            Collections.sort(articles, Comparator.comparingDouble(Article::getImpactFactor).reversed())
        }
    }

    def articles_query(searchTerm) {
        def eUtilsSearchResult   = EntrezEUtils.entrezSearch(searchTerm: searchTerm)
        //Thread.sleep(1000) //Request limiter
        def eSearchResult = eUtilsSearchResult.eUtilsResult   
        if (!eSearchResult) { 
            System.err.println("Quitting: Unable to maintain connection to EntrezEUtils site!")
            System.exit(-1)
        }
        def recCount = eSearchResult.Count.text().toInteger()
        println("Found ${recCount} Articles to process")
        def restart = 0
        while (recCount > 0) {
            print("..")
            def eUtilsFetchResult = EntrezEUtils.entrezFetch(eUtilsSearchResult: eUtilsSearchResult, restart: restart.toString())
                print(".")
                article_details(eUtilsFetchResult)
                println(".")
                author_details(eUtilsFetchResult)
                println("Done")
                restart  +=200 //Increment record position by 200
                recCount -=200 //Pull the next 200 records or however many are remaining
        }
    }

    def article_details(def eUtilsFetchResult) {
        def eFetchResult = eUtilsFetchResult.eUtilsResult  
        def pubmedArticles = eFetchResult.PubmedArticle
        
        pubmedArticles.each { article ->
            def pmid = article.MedlineCitation.PMID.text()
            def paper = article.MedlineCitation.Article 
            def issn = paper.Journal.ISSN.find { it.@IssnType == 'Electronic' }?.text() ?: ""
            def eissn = paper.Journal.ISSN.find { it.@IssnType == 'Print' }?.text() ?: ""
            def pubYear = paper.Journal.JournalIssue.PubDate.Year.text()
            def pubAbbr = paper.Journal.ISOAbbreviation.text()
            def title   = paper.ArticleTitle.text()
            def aAbstract = paper.Abstract.AbstractText.text()
            def impactFactorDB = ImpactFactorDB.getInstance()
            def impactFactor = impactFactorDB.getImpactFactor(issn) ?: impactFactorDB.getImpactFactor(eissn) 

            //def outputLine = "${pmid},${issn},${eissn},${pubYear},\"${pubAbbr}\",\"${title}\",${impactFactor}\n"
            print(".")
            articles.add(new Article(pmid, issn, eissn, pubYear, pubAbbr, title, aAbstract, impactFactor))
        }
    }

    def author_details(def eUtilsFetchResult) {
        def eFetchResult = eUtilsFetchResult.eUtilsResult  
        def articles = eFetchResult.PubmedArticle
        
        articles.each { article ->
            def pmid = article.MedlineCitation.PMID.text()
            def authorList = article.MedlineCitation.Article.AuthorList.Author.findAll()

            authorList.eachWithIndex { author, i ->
                def lastName = author.LastName.text() ?: ""
                def firstName = author.ForeName.text() ?: ""
                def initials = author.Initials.text() ?: ""
                def orcid = author.Identifier.find { it.@Source == 'ORCID' }?.text() ?: ""
                def authorPos = (i == authorList.size() - 1) ? "999" : (i + 1).toString()
                def affiliation = author.AffiliationInfo.Affiliation.text() ?: ""

                //def outputLine = "${pmid},${lastName},${firstName},${initials},\"${affiliation}\",${orcid},${authorPos}\n"
                println("Add Author ${pmid},${lastName} ")
                authors.add(new Author(pmid,lastName,firstName,initials,affiliation,orcid,authorPos))
            }
        }
    }



    def toCSV(boolean append = false) {
        toCSVItems.each { items ->
        if (items.size() > 0) {
                items.sort()
                def fileName = items[0].csvFileName()
                def csvFile = new File(fileName)
                //if fileExists==true and append==true -> writeHeader should be false
                def printHeader = !(csvFile.exists() && append) 

                csvFile.newWriter(append).withWriter { writer ->
                    if(printHeader) {
                        writer.println(items[0].csvHeader()) // Write header
                    }

                    items.each { item ->
                        writer.println(item.toCSVString())
                    }
                }
                println "CSV file '$fileName' written successfully."
            }
        }
    }


}
