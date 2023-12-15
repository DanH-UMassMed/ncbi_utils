package med_chat.ncbi

import med_chat.impact_factor.ImpactFactorDB
        
class ArticlesManager {
    def articles = [] as List<Article>
    def authors = [] as List<Author>
    def references = [] as List<Reference>
    def toCSVItems = [authors, articles, references]

    ArticlesManager(List<Article> initialArticles = []) {
        articles.addAll(initialArticles)

        // Add a sort method to the articles list (Sort on Impact Factor)
        articles.metaClass.sort = {
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
                print(".")
                reference_details(eUtilsFetchResult)
                println("Done")
                restart  +=200 //Increment record position by 200
                recCount -=200 //Pull the next 200 records or however many are remaining
        }
    }

    def article_details(def eUtilsFetchResult) {
        def eFetchResult = eUtilsFetchResult.eUtilsResult  
        def pubmedArticles = eFetchResult.PubmedArticle
        
        pubmedArticles.each { article ->
            articles.add(Article.createArticle(article))
        }
    }

    def author_details(def eUtilsFetchResult) {
        def eFetchResult = eUtilsFetchResult.eUtilsResult  
        def pubmedArticles = eFetchResult.PubmedArticle
        
        pubmedArticles.each { article ->
            def pmid = article.MedlineCitation.PMID.text()
            def authorList = article.MedlineCitation.Article.AuthorList.Author.findAll()

            authorList.eachWithIndex { author, i ->
                def String lastAuthorPos="999" //Make sure we know the last Author given any number of Authors
                def authorPos = (i == authorList.size() - 1) ? lastAuthorPos : (i + 1).toString()
                authors.add(Author.createAuthor(author, pmid, authorPos))
            }
        }
    }

    def reference_details(def eUtilsFetchResult) {
        def eFetchResult = eUtilsFetchResult.eUtilsResult  
        def pubmedArticles = eFetchResult.PubmedArticle
        
        pubmedArticles.each { article ->
            def pmid = article.MedlineCitation.PMID.text()
            def referenceList = article.PubmedData.ReferenceList.Reference.findAll()

            referenceList.each { reference ->
                references.add(Reference.createReference(reference, pmid)) 
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
