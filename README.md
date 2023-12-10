# ncbi_utils
ncbi_utils
https://www.ncbi.nlm.nih.gov/books/NBK25501/

![Tbale1](https://www.ncbi.nlm.nih.gov/books/NBK25497/table/chapter2.T._entrez_unique_identifiers_ui/?report=objectonly)


# eInfo
https://eutils.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi?db=pmc

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi?db=nlmcatalog

# eSearch

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=20&term=Dennis+Voelker

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nlmcatalog&term=journalspmc[All%20Fields]

### Search nlmcatalog only get pubmed journals
https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nlmcatalog&term=nlmcatalog%20pubmed[subset]

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nlmcatalog&term=nlmcatalog%20pubmed[subset]%20Cell Metabolism[All%20Fields]


# eSummary
https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=37144896


# eFetch

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nlmcatalog&retmode=xml&id=101682128,101233170

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nlmcatalog&retmode=xml&id=297618,7505876

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=34577062,24475906&rettype=fasta&retmode=text

https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&query_key=1&WebEnv=MCID_6570ac6193da3159df3e2274

# Pull Article

https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8322431/pdf/41598_2021_Article_94902.pdf


https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
        efetch_params = {
            "db": "pubmed",
            "id": ",".join(papers),  # Join IDs with comma
            "retmode": "json",
            "rettype": "abstract"
        }