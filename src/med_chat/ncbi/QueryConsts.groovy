package med_chat.ncbi

class QueryConsts {
    static final String PUBLISHED_RANGE  = "(2018[Date - Publication] : 2024[Date - Publication])"
    static final String PUBLISHED_W_IN_5_YRS = "(y_5[Filter])"
    static final String JOURNALS ="""
        (Cell[Journal] OR
        Cell Metabolism[Journal] OR
        Molecular Cell[Journal] OR
        Cell Reports[Journal] OR
        Cell Systems[Journal] OR
        Current Biology[Journal] OR
        Developmental Cell[Journal] OR
        Structure[Journal] OR
        Nature[Journal] OR
        Nature Cell Biology[Journal] OR
        Nature Metabolism[Journal] OR
        Nature Aging[Journal] OR
        Nature Communications[Journal] OR
        Science[Journal] OR
        Science Signaling[Journal] OR
        Science Advances[Journal] OR
        PLoS Biology[Journal] OR
        PLoS Genetics[Journal] OR
        Nature Reviews Molecular Cell Biology[Journal] OR
        Nature Reviews Genetics[Journal] OR
        Trends in Endocrinology and Metabolism[Journal] OR
        Trends in Genetics[Journal] OR
        Trends in Biochemical Sciences[Journal] OR
        Trends in Cell Biology[Journal] OR
        Annual Review of Genetics[Journal] OR
        Annual Review of Biochemistry[Journal] OR
        Annual Review of Cell and Developmental Biology[Journal] OR
        Annual Review of Nutrition[Journal] OR
        Journal of Lipid Research[Journal] OR
        Journal of Cell Biology[Journal] OR
        Molecular and Cellular Biology[Journal] OR
        Journal of Biological Chemistry[Journal] OR
        Proceedings of the National Academy of Sciences[Journal] OR
        eLife[Journal] OR
        The EMBO Journal[Journal] OR
        Nucleic Acids Research[Journal] OR
        Journal of Cell Science[Journal] OR
        Developmental Biology[Journal] OR
        Genes and Development[Journal])
        """
    // Call the function with the search term
    static final String AMYS_PAPERS = """((walker ak[Author]) NOT (adam k walker[Author]) NOT (Angela K Walker) NOT (Allison K Walker) 
                       NOT (Alexandra K Walker) NOT (Alexis K Walker) NOT (Aaron K Walker) NOT (Anaesthesia[jour]) 
                       NOT (24335193[UID]) NOT (8531707[UID]) NOT (22267235[UID]) NOT (25264390[UID]) NOT (24777035[UID]) 
                       NOT (17896077[UID]) NOT (24751964[UID]) NOT (21907243[UID]) NOT (21438770[UID]) NOT (11332761[UID]) 
                       NOT (20666652[UID]) NOT (17337586[UID]) NOT (9921134[UID]) NOT (10419626[UID]) NOT (10548814[UID]) 
                       NOT (8700510[UID])  NOT (10631665[UID])) OR (21124729[UID])"""
}
