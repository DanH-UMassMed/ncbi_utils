// Function to calculate similarity score based on Levenshtein distance
def calculateSimilarity(String text1, String text2) {
    def lengthText1 = text1.length() + 1
    def lengthText2 = text2.length() + 1

    def matrix = new int[lengthText1][lengthText2]

    for (int i = 0; i < lengthText1; i++) {
        matrix[i][0] = i
    }

    for (int j = 0; j < lengthText2; j++) {
        matrix[0][j] = j
    }

    for (int i = 1; i < lengthText1; i++) {
        for (int j = 1; j < lengthText2; j++) {
            def cost = (text1[i - 1] == text2[j - 1]) ? 0 : 1

            matrix[i][j] = Math.min(
                Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                matrix[i - 1][j - 1] + cost
            )
        }
    }

    def maxLength = Math.max(text1.length(), text2.length())
    def similarityScore = 1 - (matrix[lengthText1 - 1][lengthText2 - 1] / maxLength as double)
    
    return similarityScore
}

// Function to calculate cosine similarity
def calculateCosineSimilarity(String text1, String text2) {
    def tokenize = { String text -> text.tokenize() } // Tokenize text into words

    // Tokenize texts
    def words1 = tokenize(text1.toLowerCase())
    def words2 = tokenize(text2.toLowerCase())

    // Combine words into a set to find the unique set of words in both texts
    def combinedWords = (words1 + words2).unique()

    // Create vectors representing word frequencies in each text
    def vector1 = combinedWords.collect { word -> words1.count(word) }
    def vector2 = combinedWords.collect { word -> words2.count(word) }

    // Calculate dot product
    def dotProduct = (0..<combinedWords.size()).sum { i -> vector1[i] * vector2[i] }

    // Calculate magnitudes of vectors
    def magnitude1 = Math.sqrt(vector1.sum { it * it } as double)
    def magnitude2 = Math.sqrt(vector2.sum { it * it } as double)

    // Calculate cosine similarity
    def cosineSimilarity = dotProduct / (magnitude1 * magnitude2)

    return cosineSimilarity
}

// Function to calculate Jaccard similarity
def calculateJaccardSimilarity(String text1, String text2) {
    def tokenize = { String text -> text.tokenize() } // Tokenize text into words

    // Tokenize texts
    def words1 = tokenize(text1.toLowerCase())
    def words2 = tokenize(text2.toLowerCase())

    // Convert words into sets to find the unique set of words in both texts
    def set1 = words1 as Set
    def set2 = words2 as Set

    // Calculate Jaccard similarity
    def intersection = set1.intersect(set2).size()
    def union = set1.size() + set2.size() - intersection

    def jaccardSimilarity = (intersection as double) / union
    return jaccardSimilarity
}


// Example usage:
def similarity = ""
def text1 = "This is the first piece of text."
def text2 = "Here is another piece of text."
def text3 = "Her is another piece of text."
println("STARTING")
similarity = calculateSimilarity(text1, text2)
println "Similarity score: ${similarity}"
similarity = calculateSimilarity(text2, text3)
println "Similarity score: ${similarity}"
similarity = calculateSimilarity(text3, text3)
println "Similarity score: ${similarity}"
println("======================================")

similarity = calculateCosineSimilarity(text1, text2)
println "Cosine Similarity: ${similarity}"
similarity = calculateCosineSimilarity(text2, text3)
println "Cosine Similarity: ${similarity}"
similarity = calculateCosineSimilarity(text3, text3)
println "Cosine Similarity: ${similarity}"
println("======================================")

similarity = calculateJaccardSimilarity(text1, text2)
println "Jaccard Similarity: ${similarity}"
similarity = calculateJaccardSimilarity(text2, text3)
println "Jaccard Similarity: ${similarity}"
similarity = calculateJaccardSimilarity(text3, text3)
println "Jaccard Similarity: ${similarity}"
