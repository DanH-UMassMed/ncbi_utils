class Hello {
    static String method1() {
        return "Hello"
    }

    static String method2(String name) {
        def hello = method1()
        return hello + " " + name
    }
}

// Example usage:
def message = Hello.method2("John")
println message // Output: Hello John
