package evidence_file_tools;

import fasta_tools.FastaUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class SeleEvidenceKcrPepti31Utils {
    public static void main(String args[]){
        String abundKcrEvidenceFile="human_abundKcrEvidence_column.txt";
        double minProbScore=0.1;
        String swissProtDBFile="uniprot-human-filtered-reviewed_yes_110.fasta";
        int size=15;

        //getMinScoreKcrUniPeptiFile(abundKcrEvidenceFile, minProbScore, swissProtDBFile, size);
        //getMinScoreKcrMultiPeptiFile(abundKcrEvidenceFile, minProbScore, swissProtDBFile, size);
    }

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
    {
        List<PeptiCrotKProbInfo> abundKcrPeptiSiteList=loadAbundEvidenceKcrList(abundKcrEvidenceFile);
        Hashtable<String,String> protAccessHt=peptiCrotKProbList2ProtHt(abundKcrPeptiSiteList);
        Hashtable<String, String> prot2SequHt=FastaUtils.loadProAcc2SequHt(swissProtDBFile, protAccessHt);

        Hashtable<String,String> pepti2ProtHt=new Hashtable<>();
        for(int i=0;i<abundKcrPeptiSiteList.size();i++)
        {
            List<CrotKSiteInfo> allCrotKSiteList=abundKcrPeptiSiteList.get(i).getCrotKSiteList();

            List<CrotKSiteInfo> seleCrotKSiteList=PeptiCrotKProbTools.scoreCrotKSiteList(allCrotKSiteList,minProbScore);
            if(seleCrotKSiteList.size()==0)
                continue;

            String peptide=abundKcrPeptiSiteList.get(i).getPeptide();
            String proteins=abundKcrPeptiSiteList.get(i).getProteins();
            List<String> pepti31List=null;
            if(!proteins.contains(";"))
            {
                if(!prot2SequHt.containsKey(proteins)) {
                    System.out.println(proteins+" not find!");
                    continue;
                }

                String sequence=prot2SequHt.get(proteins);
                pepti31List=uniProtCrotK2Pepti31(seleCrotKSiteList,peptide, proteins, sequence, size);
            }
            else
                pepti31List=protClustCrotK2UniPepti31(seleCrotKSiteList,peptide,proteins,prot2SequHt,size);

            if(pepti31List!=null) {
                pepti2ProtHt = addPeptiList2Ht(pepti31List, proteins,pepti2ProtHt);
            }
        }

        SeleNoKcrEvidenceTools.writeHash(pepti2ProtHt, abundKcrEvidenceFile+"_uniPepti31_cut"+minProbScore+".xls");
    }

    /**
     * Generate positive dataset from the selected evidence.txt file
     * different from the above getMinScoreKcrUniPeptiFile method, the uniqueness of 31-residue windows
     * is not required in this method
     *
     * @param abundKcrEvidenceFile selected "Sequence,Length,Modifications,Modified sequence,Crot(K) Probabilities,roteins" columns from evidence.txt file
     * @param minProbScore Kcr-Score in "Crot(K) Probabilities" column
     * @param swissProtDBFile download sequence from UniProt
     * @param size The length of the sequence on both sides of the modified site
     */
    public static void getMinScoreKcrMultiPeptiFile(String abundKcrEvidenceFile, double minProbScore,
                                                   String swissProtDBFile, int size)
    {
        List<PeptiCrotKProbInfo> abundKcrPeptiSiteList=loadAbundEvidenceKcrList(abundKcrEvidenceFile);
        Hashtable<String,String> protAccessHt=peptiCrotKProbList2ProtHt(abundKcrPeptiSiteList);
        Hashtable<String, String> prot2SequHt=FastaUtils.loadProAcc2SequHt(swissProtDBFile, protAccessHt);

        Hashtable<String,String> allPepti2ProtHt=new Hashtable<>();
        for(int i=0;i<abundKcrPeptiSiteList.size();i++)
        {
            List<CrotKSiteInfo> allCrotKSiteList=abundKcrPeptiSiteList.get(i).getCrotKSiteList();

            List<CrotKSiteInfo> seleCrotKSiteList=PeptiCrotKProbTools.scoreCrotKSiteList(allCrotKSiteList,minProbScore);
            if(seleCrotKSiteList.size()==0)
                continue;

            String peptide=abundKcrPeptiSiteList.get(i).getPeptide();
            String proteins=abundKcrPeptiSiteList.get(i).getProteins();
            List<String> pepti31List=null;
            if(!proteins.contains(";"))
            {
                if(!prot2SequHt.containsKey(proteins)) {
                    System.out.println(proteins+" not find!");
                    continue;
                }

                String sequence=prot2SequHt.get(proteins);
                pepti31List=uniProtCrotK2Pepti31(seleCrotKSiteList,peptide, proteins, sequence, size);
                if(pepti31List!=null) {
                    allPepti2ProtHt = addPeptiList2Ht(pepti31List, proteins,allPepti2ProtHt);
                }
            }
            else {
                Hashtable<String, String> pepti31Samp2ProtHt = protClustCrotK2MuitiPepti31(seleCrotKSiteList, peptide, proteins, prot2SequHt, size);
                allPepti2ProtHt=addPeptiHt2AllHt(pepti31Samp2ProtHt,allPepti2ProtHt);
            }
        }
        SeleNoKcrEvidenceTools.writeHash(allPepti2ProtHt, abundKcrEvidenceFile+"_mulPepti31_cut"+minProbScore+".xls");
    }


    public static List<PeptiCrotKProbInfo> loadAbundEvidenceKcrList(String abundKcrEvidenceFile)
    {
        List<PeptiCrotKProbInfo> abundKcrPeptiSiteList=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            abundKcrPeptiSiteList=new ArrayList<>();
            fis = new FileInputStream(abundKcrEvidenceFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            Hashtable<String,String> crotKPeptiHt=new Hashtable<>();
            String line = br.readLine();
            br.readLine();//Sequence	Length	K Count	R Count	Modifications	Modified sequence	Crot(K) Probabilities	Proteins
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;

                String[] tmpArry=line.split("\t");
                String peptide=tmpArry[0].trim();
                String modify=tmpArry[2].trim();
                String crotKProb=tmpArry[4].trim();
                String proteins=tmpArry[5].trim();

                if(!modify.contains("Crot(K)") && !modify.contains("Crotony(K)"))
                    continue;
                if(crotKPeptiHt.containsKey(crotKProb))
                    continue;
                else
                    crotKPeptiHt.put(crotKProb,"");

                List<CrotKSiteInfo> seleCrotKSiteList=PeptiCrotKProbTools.crotKProb2SiteInfoList(crotKProb);
                PeptiCrotKProbInfo peptiCrotInfo=new PeptiCrotKProbInfo();
                peptiCrotInfo.setCrotKSiteList(seleCrotKSiteList);
                peptiCrotInfo.setPeptide(peptide);
                peptiCrotInfo.setProteins(proteins);

                abundKcrPeptiSiteList.add(peptiCrotInfo);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+abundKcrEvidenceFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+abundKcrEvidenceFile);
        }

        return abundKcrPeptiSiteList;
    }

    public static Hashtable<String,String> peptiCrotKProbList2ProtHt(List<PeptiCrotKProbInfo> peptiCrotKSiteList)
    {
        Hashtable<String,String> protHt=new Hashtable<>();
        for(int i=0;i<peptiCrotKSiteList.size();i++)
        {
            String protAccessClust=peptiCrotKSiteList.get(i).getProteins();
            if(protAccessClust.contains(";"))
            {
                String[] tmpArry=protAccessClust.split(";");
                for(int j=0;j<tmpArry.length;j++)
                    protHt.put(tmpArry[j].trim(),"");
            }
            else
                protHt.put(protAccessClust,"");
        }
        return protHt;
    }

    public static List<String> uniProtCrotK2Pepti31(List<CrotKSiteInfo> seleCrotKSiteList,
                                            String pepti, String protAccess,String sequence, int size)
    {
        List<String> pepti31SampList=new ArrayList<>();
        if(!sequence.contains(pepti))
        {
            System.out.println(pepti+" not in "+protAccess);
            return null;
        }

        int startIdx=sequence.indexOf(pepti);
        for(int i=0;i<seleCrotKSiteList.size();i++)
        {
            int crotKIdx=seleCrotKSiteList.get(i).getPeptiSite();
            String pepti31=SeleNoKcrEvidenceTools.getPepti31Str(crotKIdx, sequence, startIdx, size);
            if(pepti31!=null)
                pepti31SampList.add(pepti31);
        }
        return pepti31SampList;
    }

    public static List<String> protClustCrotK2UniPepti31(List<CrotKSiteInfo> seleCrotKSiteList,
                                       String pepti, String protClust, Hashtable<String, String> prot2SequHt, int size)
    {
        String[] proteinArry = protClust.split(";");
        List<String> allSamePepti31List=new ArrayList<>();
        for(int kcrSiteIdx=0;kcrSiteIdx<seleCrotKSiteList.size();kcrSiteIdx++)
        {
            Hashtable<String,String> currKcrSitePepti31Ht=new Hashtable<>();
            for (int proIdx = 0; proIdx < proteinArry.length; proIdx++)
            {
                String protAccess = proteinArry[proIdx];
                if(protAccess.startsWith("CON_"))
                    continue;
                if(!prot2SequHt.containsKey(protAccess))
                {
                    System.out.println(protAccess+" no sequ");
                    continue;
                }

                String sequence = prot2SequHt.get(protAccess);
                if (!sequence.contains(pepti)) {
                    System.out.println(pepti + " not in " + protAccess);
                    continue;
                }

                int startIdx = sequence.indexOf(pepti);
                int crotKIdx = seleCrotKSiteList.get(kcrSiteIdx).getPeptiSite();
                String currCrotSitePepti31 = SeleNoKcrEvidenceTools.getPepti31Str(crotKIdx, sequence, startIdx, size);
                if (currCrotSitePepti31 != null)
                    currKcrSitePepti31Ht.put(currCrotSitePepti31,protAccess);
            }

            if(currKcrSitePepti31Ht.size()==1)
            {
                Enumeration pepti31Sets = currKcrSitePepti31Ht.keys();
                while (pepti31Sets.hasMoreElements()) {
                    String pepti31 = (String) pepti31Sets.nextElement();
                    allSamePepti31List.add(pepti31);
                }
            }
            else {
                //System.out.println(protClust+" multi pepti31!");
            }
        }
        return allSamePepti31List;
    }

    public static Hashtable<String,String> protClustCrotK2MuitiPepti31(List<CrotKSiteInfo> seleCrotKSiteList,
                                                         String pepti, String protClust, Hashtable<String, String> prot2SequHt, int size)
    {
        String[] proteinArry = protClust.split(";");
        Hashtable<String,String> allPepti31Samp2ProtHt=new Hashtable<>();
        for(int kcrSiteIdx=0;kcrSiteIdx<seleCrotKSiteList.size();kcrSiteIdx++)
        {
            for (int proIdx = 0; proIdx < proteinArry.length; proIdx++)
            {
                String protAccess = proteinArry[proIdx];
                if(protAccess.startsWith("CON_"))
                    continue;
                if(!prot2SequHt.containsKey(protAccess))
                {
                    System.out.println(protAccess+" no sequ");
                    continue;
                }

                String sequence = prot2SequHt.get(protAccess);
                if (!sequence.contains(pepti)) {
                    System.out.println(pepti + " not in " + protAccess);
                    continue;
                }

                int startIdx = sequence.indexOf(pepti);
                int crotKIdx = seleCrotKSiteList.get(kcrSiteIdx).getPeptiSite();
                String currCrotSitePepti31 = SeleNoKcrEvidenceTools.getPepti31Str(crotKIdx, sequence, startIdx, size);
                if (currCrotSitePepti31 != null)
                    allPepti31Samp2ProtHt.put(currCrotSitePepti31,protAccess);
            }
        }
        return allPepti31Samp2ProtHt;
    }

    public static Hashtable<String,String> addPeptiList2Ht(List<String> peptiList, String proteins,
                                                           Hashtable<String,String> pepti2AccessHt)
    {
        for(int i=0;i<peptiList.size();i++)
        {
            pepti2AccessHt.put(peptiList.get(i),proteins);
        }
        return pepti2AccessHt;
    }

    public static Hashtable<String,String> addPeptiHt2AllHt(Hashtable<String,String> pepti31Samp2ProtHt,
                                                              Hashtable<String,String> allPepti2AccessHt)
    {
        Enumeration pepti31Sets = pepti31Samp2ProtHt.keys();
        while (pepti31Sets.hasMoreElements()) {
            String pepti31 = (String) pepti31Sets.nextElement();
            String access = pepti31Samp2ProtHt.get(pepti31);
            allPepti2AccessHt.put(pepti31,access);
        }
        return allPepti2AccessHt;
    }
}
