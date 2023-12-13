package med_chat.utils
/* 
   Do appropriate logging at some point 
   Use this Class as a stop Gap for now
*/
class Logger {
    private File logFile
    private static String logFileName='./output/log.txt'
    
    // Default constructor
    Logger(String fileName = null) {
        if(!fileName)
            fileName = logFileName

        logFile = new File(fileName)
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }
    
    // Method to log a message to the file
    def log(String message) {
        logFile.append(message + '\n')
    }

    static def delete_log_file() {
        def file = new File(logFileName)
        if (file.exists()) {
            file.delete()
        }
    }
}
