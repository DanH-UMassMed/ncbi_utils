// Define a methodParams map with or without 'retmax'
def Map methodParams = [:]
methodParams['retmax'] = "300"


// Get 'retmax' value using Elvis operator
def retmax1 = methodParams.retmax ?: "200"
def retmax2 = methodParams.retmax2 ?: "200"

// Print the values
println "retmax1: $retmax1" // Output: retmax1: 300 (since methodParams1 has 'retmax')
println "retmax2: $retmax2" // Output: retmax2: 200 (since methodParams2 does not have 'retmax')
