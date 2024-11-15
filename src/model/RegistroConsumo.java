import java.util.Date;

public class RegistroConsumo {
    private Date data;
    private float valor;
    private String descricao;  // Nova descrição detalhada do consumo

    // Construtor atualizado
    public RegistroConsumo(Date data, float valor, String descricao) {
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}