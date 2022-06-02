package rewards;

public class EthicalRewardQuad {
    
    private Integer nabla ;
    private Integer barredNabla;
    private Integer triangle;
    private Integer barredTriangle;

    public EthicalRewardQuad(){
        nabla = 0;
        barredNabla = 0;
        triangle = 0;
        barredTriangle = 0;
    }

    public Integer getNabla() {
        return nabla;
    }

    public Integer getBarredNabla() {
        return barredNabla;
    }

    public Integer getTriangle() {
        return triangle;
    }

    public Integer getBarredTriangle() {
        return barredTriangle;
    }

    public void setNabla(Integer nabla) {
        this.nabla = nabla;
    }

    public void setBarredNabla(Integer nablaBarre) {
        this.barredNabla = nablaBarre;
    }

    public void setTriangle(Integer triangle) {
        this.triangle = triangle;
    }

    public void setBarredTriangle(Integer triangleBarre) {
        this.barredTriangle = triangleBarre;
    }

    public void incrementNabla(){
        nabla ++;
    }

    public void incrementBarredNabla(){
        barredNabla ++;
    }

    public void incrementTriangle(){
        triangle ++;
    }

    public void incrementBarredTriangle(){
        barredTriangle ++;
    }

    @Override
    public String toString() {
        String tmp = "";
        tmp += "Nabla : " + nabla + " , BarredNabla : " + barredNabla + " , Triangle : " + triangle + " , BarredTriangle : " + barredTriangle;
        return tmp;
    }
}
