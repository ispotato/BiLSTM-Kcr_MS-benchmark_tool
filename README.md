# MS-based-benchmark
This java project is used to handle the mass spectrometry result file evidence.txt generated from the MaxQuant software, and generate the MS-based negataive and positive datasets which can be used for training the machine learning models for prediction of protein modifications.


//Generate MS-based positive dataset:
getMinScoreKcrUniPeptiFile(String abundKcrEvidenceFile, double minProbScore, String swissProtDBFile, int size)
                                   
//Generate MS-based negative dataset:                                   
getNoKcrEvidenPepti31SampList(String evidenSubListFile, String swissProtDBFile, int size)
    
