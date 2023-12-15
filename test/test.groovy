@Grab('com.opencsv:opencsv:5.5')
import com.opencsv.CSVReader
import com.opencsv.CSVWriter

List<List<String>> getRowsWithMultipleMatches(List<List<String>> rows) {
    def targetCounts = [:].withDefault { 0 }

    rows.each { row ->
        def target = row[1]
        targetCounts[target]++
    }

    def multipleMatches = targetCounts.findAll { target, count ->
        count > 1
    }.collect { target, count ->
        target
    }

    return rows.findAll { row ->
        multipleMatches.contains(row[1])
    }
}

// Read data from the CSV file
def inputFile = new File('./output/references.csv')
def reader = new CSVReader(inputFile.newReader())

def rows = reader.readAll()

reader.close()

// Filter rows with multiple matches in the target column
def rowsWithMultipleMatches = getRowsWithMultipleMatches(rows)

// Write the subsetted data to a new CSV file
def outputFile = new File('d3js/references.csv')
def writer = new CSVWriter(outputFile.newWriter())

writer.writeAll(rowsWithMultipleMatches)
writer.close()

println("Subsetted data has been written to 'output.csv'")
