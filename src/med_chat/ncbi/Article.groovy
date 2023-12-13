package med_chat.ncbi
        
class Article {
    private String pmid;
    private String issn;
    private String eissn;
    private String pubYear;
    private String pubAbbr;
    private String title;
    private String aAbstract;
    private double impactFactor;

    public Article(String pmid, String issn, String eissn, String pubYear, String pubAbbr, String title, String aAbstract, String impactFactor) {
        this.pmid = pmid;
        this.issn = issn;
        this.eissn = eissn;
        this.pubYear = pubYear;
        this.pubAbbr = pubAbbr;
        this.title = title;
        this.aAbstract = aAbstract;
        this.impactFactor = impactFactor.toDouble();
    }

    public double getImpactFactor() {
        return impactFactor;
    }

    @Override
    public String toString() {
        return pmid + "," + issn + "," + eissn + "," + pubYear + "," + pubAbbr + "," + title + "," + impactFactor;
    }

    String toCSVString() {
        "$pmid,$issn,$eissn,$pubYear,\"$pubAbbr\",\"$title\",$impactFactor"
    }

    String csvHeader() {
        "pmid,issn,eissn,pub_year,pub_abbr,title,impact_factor"
    }

    String csvFileName() {
        "./output/articles.csv"
    }
    
}