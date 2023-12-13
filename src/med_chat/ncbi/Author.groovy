package med_chat.ncbi

class Author {
    String pmid
    String lastName
    String firstName
    String initials
    String affiliation
    String orcid
    String authorPos

    Author(String pmid, String lastName, String firstName, String initials, String affiliation, String orcid, String authorPos) {
        this.pmid = pmid
        this.lastName = lastName
        this.firstName = firstName
        this.initials = initials
        this.affiliation = affiliation
        this.orcid = orcid
        this.authorPos = authorPos
    }

    String toCSVString() {
        "${pmid},${orcid},${lastName},${firstName},${initials},${authorPos},\"${affiliation}\""
    }
    String csvHeader() {
        "pmid,orcid,last_name,first_name,initials,author_pos,affiliation"
    }
    String csvFileName() {
        "./output/authors.csv"
    }
}
