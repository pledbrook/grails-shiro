import org.apache.shiro.crypto.hash.Sha1Hash

class BootStrap {
    def init = { servletContext ->
        // Some initial books that we can test against.
        new Book(title: "Colossus", author: "Niall Ferguson").save()
        new Book(title: "Misery", author: "Stephen King").save()
        new Book(title: "Guns, Germs, and Steel", author: "Jared Diamond").save()
    }

    def destroy = {
    }
} 
