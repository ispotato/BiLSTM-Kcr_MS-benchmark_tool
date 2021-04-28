# MS-based-benchmark
This java project is used to handle the mass spectrometry result file evidence.txt generated from the MaxQuant software, and generate the MS-based negataive and positive datasets which can be used for training the machine learning models for prediction of protein modifications.


/**
     * Generate positive dataset from the selected evidence.txt file
     * To ensure the reliability of these samples, when the peptide containing the non-Kcr site corresponded to
     * multiple proteins in the evidence.txt file, we only retained the negative samples that had identical
     * 31-residue windows in all proteins. Through this selection on candidate negative sets, we defined the
     * MS-based negative sample set as the “MS-based nega-set.”
     *
     * @param abundKcrEvidenceFile selected "Sequence,Length,Modifications,Modified sequence,Crot(K) Probabilities,roteins" columns from evidence.txt file
     * @param minProbScore Kcr-Score in "Crot(K) Probabilities" column
     * @param swissProtDBFile download sequence from UniProt
     * @param size The length of the sequence on both sides of the modified site
     */
    public static void getMinScoreKcrUniPeptiFile(String abundKcrEvidenceFile, double minProbScore,
                                                  String swissProtDBFile, int size)
                                                  
/**
     * Generate MS-based non-Kcr negative dataset from the selected evidence.txt file
     *
     * @param evidenSubListFile selected "Sequence,Length,Modifications,Modified sequence,Crot(K) Probabilities,roteins" columns from evidence.txt file
     * @param swissProtDBFile download sequence from UniProt
     * @param size The length of the sequence on both sides of the modified site
     */
    public static void getNoKcrEvidenPepti31SampList(String evidenSubListFile, String swissProtDBFile, int size)
    
