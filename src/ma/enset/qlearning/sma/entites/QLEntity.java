package ma.enset.qlearning.sma.entites;

public class QLEntity implements Comparable{
    private String name;
    private int stepsNb;

    public QLEntity(String name, int stepsNb) {
        this.name = name;
        this.stepsNb = stepsNb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStepsNb() {
        return stepsNb;
    }

    public void setStepsNb(int stepsNb) {
        this.stepsNb = stepsNb;
    }

    @Override
    public int compareTo(Object o) {
        QLEntity qlEntity=(QLEntity) o;
        if (this.stepsNb>qlEntity.stepsNb)
            return 1;
        else if(this.stepsNb< qlEntity.stepsNb){
            return -1;
        }else
            return 0;
    }
}
