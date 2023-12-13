package med_chat.impact_factor

@Grab('com.opencsv:opencsv:5.5')
import com.opencsv.CSVReader

class ImpactFactorDB {
    static ImpactFactorDB instance
    def issnMap = [:]
    def eissnMap = [:]

    static ImpactFactorDB getInstance() {
        if (!instance) {
            instance = new ImpactFactorDB()
        }
        return instance
    }

    ImpactFactorDB(String impact_factor_db = "2022_JCR_IF.csv") {
        def classLoader = this.class.classLoader
        def currentClass = this.getClass()
        def packageName = currentClass.getPackage().name
        def packagePath = packageName.replaceAll('\\.', '/')
        def filePath = "${packagePath}/${impact_factor_db}"
        def resource = classLoader.getResource(filePath)
        if (resource) {
            //println "Resource found at path: ${resource.path}"
            def file = new File(resource.toURI())
            def csvReader = new CSVReader(new FileReader(file))
            String[] line;
            def header = csvReader.readNext() 
            while ((line = csvReader.readNext()) != null) {
                // Process each line of the CSV file
                def journalName = line[0]
                def ISSN        = line[1]
                def JIF_2021    = line[2]
                def eISSN       = line[3]
                def category    = line[4]

                issnMap[ISSN]   = [journalName, JIF_2021, eISSN, category]
                eissnMap[eISSN] = [journalName, JIF_2021, ISSN, category]

            }
        }else{
            throw new FileNotFoundException("Resource '${filePath}' not found in package '${packageName}'.")
        }
    }

    def getImpactFactor(String issn) {
        //Assumes issn numbers are unique across print and electronic 
        if (issnMap.containsKey(issn)) {
            return issnMap[issn][1] // Return JIF_2021 from issnMap
        } else if (eissnMap.containsKey(issn)) {
            return eissnMap[issn][1] // Return JIF_2021 from eissnMap
        } else {
            return null // Or any other desired handling for missing ISSN
        }
    }
}
