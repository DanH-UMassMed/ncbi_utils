package med_chat.ncbi

import med_chat.impact_factor.ImpactFactorDB
        
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
        "https://pubmed.ncbi.nlm.nih.gov/$pmid,$issn,$eissn,$pubYear,\"$pubAbbr\",\"$title\",$impactFactor"
    }

    static String csvHeader() {
        "pmid,issn,eissn,pub_year,pub_abbr,title,impact_factor"
    }

    static String csvFileName() {
        "./output/articles.csv"
    }

    static Article createArticle(articleNode) {
        def pmid = articleNode.MedlineCitation.PMID.text()
        def paper = articleNode.MedlineCitation.Article 
        def issn = paper.Journal.ISSN.find { it.@IssnType == 'Electronic' }?.text() ?: ""
        def eissn = paper.Journal.ISSN.find { it.@IssnType == 'Print' }?.text() ?: ""
        def pubYear = paper.Journal.JournalIssue.PubDate.Year.text()
        def pubAbbr = paper.Journal.ISOAbbreviation.text()
        def title   = paper.ArticleTitle.text()
        def aAbstract = paper.Abstract.AbstractText.text()
        def impactFactorDB = ImpactFactorDB.getInstance()
        def impactFactor = impactFactorDB.getImpactFactor(issn) ?: impactFactorDB.getImpactFactor(eissn) 
        return new Article(pmid, issn, eissn, pubYear, pubAbbr, title, aAbstract, impactFactor)
    }
    
}