package evidence_file_tools;

import java.util.List;

public class PeptiCrotKProbInfo {
    private String peptide;
    private List<CrotKSiteInfo> crotKSiteList;
    private String proteins;

    public String getProteins() {
        return proteins;
    }

    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    public String getPeptide() {
        return peptide;
    }

    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }

    public List<CrotKSiteInfo> getCrotKSiteList() {
        return crotKSiteList;
    }

    public void setCrotKSiteList(List<CrotKSiteInfo> crotKSiteList) {
        this.crotKSiteList = crotKSiteList;
    }
}
