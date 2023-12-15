package med_chat.ncbi
        
class Reference {
    private String primaryPMID;
    private String linkedPMID;
    private String citation;

    public Reference(String primaryPMID, String linkedPMID, String citation) {
        this.primaryPMID = primaryPMID;
        this.linkedPMID = linkedPMID;
        this.citation = citation;
    }


    @Override
    public String toString() {
        return primaryPMID + "," + linkedPMID + "," + citation;
    }

    String toCSVString() {
        "$primaryPMID,$linkedPMID,\"$citation\""
    }

    String csvHeader() {
        "primaryPMID,linkedPMID,citation"
    }

    String csvFileName() {
        "./output/references.csv"
    }
    
    static Reference createReference(referenceNode, pmid) {
        def linkedPMID = referenceNode.ArticleIdList.ArticleId.find { it.@IdType == 'pubmed' }?.text() ?: ""
        def citation = referenceNode.Citation.text()
        return new Reference(pmid, linkedPMID, citation)
    }
}