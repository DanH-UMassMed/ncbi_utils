/* This code queries the nlmcatalog to find the Journals that will be used for identifying candidate papers for the 
   2025 Gordon Conference on Molecular and Cellular Biology of Lipids 
   The code returns the ISSN and eISSN numbers for these journals
   Downstream code will use the ISSN numbers to find the Impact Scores for the Journals
   Impact Scores will be used for sorting and only as cutoff if search results require additional pruning.
*/

import EntrezEUtils
import groovy.util.NodePrinter
import groovy.xml.XmlUtil

journals =['"Cell"[ta]',
'"Cell Metabolism"[Journal]',
'"Molecular Cell"[Journal]',
'"Cell Rep"[ta]',
'"Cell Systems"[Journal]',
'"Current Biology"[Journal]',
'"Developmental Cell"[Journal]',
'"Structure"[ta]',
'"Nature"[ta]',
'"Nat Cell Biol"[ta]',
'"Nature Metabolism"[Journal]',
'"Nature Aging"[Journal]',
'"Nature Communications"[Journal]',
'"Science"[ta]',
'"Sci Signal"[ta]',
'"Science Advances"[Journal]',
'"PLoS Biology"[Journal]',
'"PLoS Genetics"[Journal]',
'"Nature Reviews Molecular Cell Biology"[Journal]',
'"Nature Reviews Genetics"[Journal]',
'"Trends in Endocrinology and Metabolism"[Journal]',
'"Trends in Genetics"[Journal]',
'"Trends in Biochemical Sciences"[Journal]',
'"Trends Cell Biol"[ta]',
'"Annual Review of Genetics"[Journal]',
'"Annual Review of Biochemistry"[Journal]',
'"Annual Review of Cell and Developmental Biology"[Journal]',
'"Annual Review of Nutrition"[Journal]',
'"Journal of Lipid Research"[Journal]',
'"J Cell Biol"[ta]',
'"Mol Cell Biol"[ta]',
'"Journal of Biological Chemistry"[Journal]',
'"Proceedings of the National Academy of Sciences"[Journal]',
'"eLife"[Journal]',
'"The EMBO Journal"[Journal]',
'"NAR Genom Bioinform"[ta]',
'"J Cell Sci"[ta]',
'"Wiley Interdiscip Rev Dev Biol"[ta]',
'"Genes and Development"[Journal]']

//[ta]   - Stands for Title Abbreviation this is the MedlineTA field
//[tiab] - Stands for Title/Abstract
//[au]   - Stands for Author
def issn_report(def eUtilsFetchResult, searchTerm, counter) {

    def eFetchResult = eUtilsFetchResult.eUtilsResult  
    //println("eFetchResult=${eFetchResult}")
                                   
    def recordSet = eFetchResult.NLMCatalogRecord
    
    //def prettyXml = XmlUtil.serialize(recordSet)
    //print(prettyXml) 

    //def printer = new NodePrinter(new PrintWriter(System.out))
    //printer.setPreserveWhitespace(true)
    //printer.print(recordSet)

    recordSet.each { catalogRecord ->
        def nlmID = catalogRecord.NlmUniqueID.text()
        def title_abbr = catalogRecord.MedlineTA.text()
        def title = catalogRecord.TitleMain.Title.text()
        def eissn = catalogRecord.ISSN.find { it.@ValidYN == 'Y' && it.@IssnType=='Electronic' }?.text() ?: ""
        def issn = catalogRecord.ISSN.find { it.@ValidYN == 'Y' && it.@IssnType=='Print' }?.text() ?: ""
        
        println("\"${searchTerm}\",\"${nlmID}\",\"${title}\",\"${title_abbr}\",\"${eissn}\",\"${issn}\"")
    }
}


Logger.delete_log_file()
println("search_term,nlm_ID,title,title_abbr,eissn,issn")
start_pos = 0
for (int i = start_pos; i < journals.size(); i++) {
    Thread.sleep(1000) //Request limiter
    searchTerm = journals[i]
    System.err.println("Journal [Query: ${i}] ${searchTerm}")
    
    db = "nlmcatalog"
    eUtilsSearchResult   = EntrezEUtils.entrezSearch(searchTerm: searchTerm, db: db)
    def eSearchResult = eUtilsSearchResult.eUtilsResult   
    def recCount = eSearchResult.Count.text().toInteger()
    def restart = 0

    while (recCount > 0) {
        eUtilsFetchResult = EntrezEUtils.entrezFetch(eUtilsSearchResult: eUtilsSearchResult, db: db, restart: restart.toString())
        issn_report(eUtilsFetchResult,searchTerm, restart)
        restart  +=200
        recCount -=200
    }
}