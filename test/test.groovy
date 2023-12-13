def myList = ["1", "11", "2", "L"]

// Sort the list with custom logic
myList.sort { a, b ->
    def isNumericA = a.isNumber()
    def isNumericB = b.isNumber()

    if (isNumericA && isNumericB) {
        // If both elements are numeric, compare as integers
        Integer.parseInt(a) <=> Integer.parseInt(b)
    } else if (isNumericA) {
        // Numeric elements come before non-numeric elements
        -1
    } else if (isNumericB) {
        // Non-numeric elements come after numeric elements
        1
    } else {
        // Both elements are non-numeric (letters), compare as strings
        a <=> b
    }
}

println myList // Output: [1, 2, 11, L]




