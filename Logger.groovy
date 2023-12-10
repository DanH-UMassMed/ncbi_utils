class Logger {
    private File logFile
    
    // Default constructor
    Logger(String fileName = 'log.txt') {
        logFile = new File(fileName)
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }
    
    // Method to log a message to the file
    def log(String message) {
        logFile.append(message + '\n')
    }
}
