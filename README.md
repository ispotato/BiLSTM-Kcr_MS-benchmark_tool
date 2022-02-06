# BiLSTM-Kcr_MS-benchmark_tool
This java project is used to handle the mass spectrometry result file evidence.txt generated from the MaxQuant software, and generate the MS-based negataive and positive datasets which can be used for training the machine learning models for prediction of protein modifications.                                   
                                  
At present, the code is mainly used for the research of lysine crotonylation sites, and will be extended to other protein modifications in the future.                                   
                                  
Main methods and steps:                                  
                                  
//Generate MS-based positive dataset:                                   
getMinScoreKcrUniPeptiFile(String abundKcrEvidenceFile, double minProbScore, String swissProtDBFile, int size)
                                   
//Generate MS-based negative candidate dataset:                                   
getNoKcrEvidenPepti31SampList(String evidenSubListFile, String swissProtDBFile, int size)
    
//Generate MS-based negative dataset:                                   
getTruePepti31List(String noKcrPepti31ListFile, String crotKPepti31ListFile)

                                   
//Generate BiLSTM-Kcr train data:                                   
sampleSet2AcidNumSet(posiListFile, negaListFile, BiLSTM_Kcr_trainData)
    
//train BiLSTM-Kcr model:                                 
python BiLSTM_Kcr_model_train_test.py


                                  
