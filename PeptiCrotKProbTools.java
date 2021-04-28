package evidence_file_tools;

import java.util.ArrayList;
import java.util.List;

public class PeptiCrotKProbTools {

    public static List<CrotKSiteInfo> crotKProb2SiteInfoList(String crotKProbStr)
    {
        List<CrotKSiteInfo> cortKSiteList=new ArrayList<>();

        String[] acidArry=crotKProbStr.split("");
        int scoreFlag=0;
        StringBuilder currentScoreSb=null;
        int currentCrotKSite=-1;
        StringBuilder peptiSb=new StringBuilder();
        for(int i=0; i<acidArry.length; i++)
        {
            if(acidArry[i].equals("("))
            {
                scoreFlag=1;
                currentScoreSb=new StringBuilder();
                currentCrotKSite=peptiSb.length()-1;
                continue;
            }
            else if(acidArry[i].equals(")"))
            {
                scoreFlag=0;
                CrotKSiteInfo crotKSite=new CrotKSiteInfo();
                crotKSite.setPeptiSite(currentCrotKSite);
                crotKSite.setProbScore(Double.parseDouble(currentScoreSb.toString()));

                cortKSiteList.add(crotKSite);
                continue;
            }

            if(scoreFlag==1)
                currentScoreSb.append(acidArry[i]);
            else
                peptiSb.append(acidArry[i]);
        }
        return cortKSiteList;
    }

    public static List<CrotKSiteInfo> scoreCrotKSiteList(List<CrotKSiteInfo> crotKSiteList, double minProbScore)
    {
        List<CrotKSiteInfo> seleKcrSiteList=new ArrayList<>();
        for(int i=0;i<crotKSiteList.size();i++)
        {
            if(crotKSiteList.get(i).getProbScore()>=minProbScore)
                seleKcrSiteList.add(crotKSiteList.get(i));
        }
        return seleKcrSiteList;
    }
}
