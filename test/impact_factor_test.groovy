import med_chat.impact_factor.ImpactFactorDB

issn="1476-4687"
impactFactorDB = ImpactFactorDB.getInstance()
impact_factor = impactFactorDB. getImpactFactor(issn)
println("${issn} has impact factor ${impact_factor}")