/* 
   This code queries the nlmcatalog to find the Journals that will be used for identifying candidate papers for the 
   2025 Gordon Conference on Molecular and Cellular Biology of Lipids 
   The code returns the ISSN and eISSN numbers for these journals
   Downstream code will use the ISSN numbers to find the Impact Scores for the Journals
   Impact Scores will be used for sorting and only as cutoff if search results require additional pruning.
*/

import med_chat.ncbi.EntrezEUtils
import med_chat.utils.Logger

import med_chat.impact_factor.ImpactFactorDB
import groovy.util.NodePrinter
import groovy.xml.XmlUtil

journals =['"Cell"[ta]',
'"Cell Metabolism"[Journal]',
'"Molecular Cell"[Journal]',
'"Cell Rep"[ta]',
'"Cell Systems"[Journal]',
'"Curr biol"[Journal]',
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
'"PNAS"[Journal]',
'"eLife"[Journal]',
'"The EMBO Journal"[Journal]',
'"NAR Genom Bioinform"[ta]',
'"J Cell Sci"[ta]',
'"Wiley Interdiscip Rev Dev Biol"[ta]',
'"Genes and Development"[Journal]']

//[ta]   - Stands for Title Abbreviation this is the MedlineTA field
//[tiab] - Stands for Title/Abstract
//[au]   - Stands for Author

def issn_report(def journals, String fileName) {
    def outputFile = new File(fileName)
    outputFile.withWriter { writer ->
        writer.write("search_term,nlm_ID,title,title_abbr,eissn,issn,impact_factor\n")
    }

    start_pos = 0
    for (int i = start_pos; i < journals.size(); i++) {
        Thread.sleep(1000) //Request limiter
        searchTerm = journals[i]
        System.err.println("A Journal [Query: ${String.format("%3d", i)}] ${searchTerm}")
        
        db = "nlmcatalog"
        eUtilsSearchResult   = EntrezEUtils.entrezSearch(searchTerm: searchTerm, db: db)
        def eSearchResult = eUtilsSearchResult.eUtilsResult   
        if (!eSearchResult) { 
            System.err.println("Quitting: Unable to maintain connection to EntrezEUtils site!")
            System.exit(-1)
        }
        def recCount = eSearchResult.Count.text().toInteger()
        def restart = 0

        while (recCount > 0) {
            eUtilsFetchResult = EntrezEUtils.entrezFetch(eUtilsSearchResult: eUtilsSearchResult, db: db, restart: restart.toString())
            issn_detail_line(eUtilsFetchResult, searchTerm, restart, fileName)
            restart  +=200 //Increment record position by 200
            recCount -=200 //Pull the next 200 records or however many are remaining
        }
    }
}

def issn_detail_line(def eUtilsFetchResult, searchTerm, counter, fileName) {
    def eFetchResult = eUtilsFetchResult.eUtilsResult  
    def recordSet = eFetchResult.NLMCatalogRecord
    
    def outputFile = new File(fileName)
    outputFile.withWriterAppend { writer ->
        recordSet.each { catalogRecord ->
            def nlmID = catalogRecord.NlmUniqueID.text()
            def title_abbr = catalogRecord.MedlineTA.text()
            def title = catalogRecord.TitleMain.Title.text()
            def eissn = catalogRecord.ISSN.find { it.@ValidYN == 'Y' && it.@IssnType=='Electronic' }?.text() ?: ""
            def issn = catalogRecord.ISSN.find { it.@ValidYN == 'Y' && it.@IssnType=='Print' }?.text() ?: ""
            def impacttFactorDB = ImpactFactorDB.getInstance()
            def impactFactor = impacttFactorDB.getImpactFactor(issn) ?: impacttFactorDB.getImpactFactor(eissn) 

            def outputLine = "\"${searchTerm}\",\"${nlmID}\",\"${title}\",\"${title_abbr}\",\"${eissn}\",\"${issn}\",${impactFactor}\n"
            System.err.println("W Journal [Query: xxx] ${searchTerm}")
            writer << outputLine
        }
    }
}

def load_impact_factor_db(String impact_factor_db) {
    def file = new File(impact_factor_db)
    def issnMap = [:]
    def iissnMap = [:]

    file.eachLine { line ->
        def parts = line.split(',')
        if (parts[0] != 'Journal Name') {
            def journalName = parts[0]
            def ISSN = parts[1]
            def JIF_2021 = parts[2]
            def eISSN = parts[3]
            def category = parts[4]

            map[ISSN] = [journalName, JIF_2021, eISSN, category]
        }
}

println map // Display the created map

}
Logger.delete_log_file()
def fileName = "./output/journals_to_query.csv"
issn_report(journals, fileName) 
