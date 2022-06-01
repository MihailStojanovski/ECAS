package rewards;

public class EthicalRewardQuad {
    
    private Integer nabla ;
    private Integer nablaBarre;
    private Integer triangle;
    private Integer triangleBarre;

    public EthicalRewardQuad(){
        nabla = 0;
        nablaBarre = 0;
        triangle = 0;
        triangleBarre = 0;
    }

    public Integer getNabla() {
        return nabla;
    }

    public Integer getNablaBarre() {
        return nablaBarre;
    }

    public Integer getTriangle() {
        return triangle;
    }

    public Integer getTriangleBarre() {
        return triangleBarre;
    }

    public void setNabla(Integer nabla) {
        this.nabla = nabla;
    }

    public void setNablaBarre(Integer nablaBarre) {
        this.nablaBarre = nablaBarre;
    }

    public void setTriangle(Integer triangle) {
        this.triangle = triangle;
    }

    public void setTriangleBarre(Integer triangleBarre) {
        this.triangleBarre = triangleBarre;
    }

    public void incrementNabla(){
        nabla ++;
    }

    public void incrementNablaBarre(){
        nablaBarre ++;
    }

    public void incrementTriangle(){
        triangle ++;
    }

    public void incrementTriangleBarre(){
        triangleBarre ++;
    }
}
