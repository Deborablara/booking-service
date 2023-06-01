package br.ufsm.poli.csi.webserver.Model;

public class Reserva {
    private String nomeReservante = null;
    private Poltrona poltrona;
    private String dataReserva = null;
    private String ip;

    public Reserva(String nomeReservante, Poltrona poltrona, String dataReserva) {
        this.nomeReservante = nomeReservante;
        this.poltrona = poltrona;
        this.dataReserva = dataReserva;
    }

    // Getters e Setters para os atributos

    public String getNomeReservante() {
        return nomeReservante;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setNomeReservante(String nomeReservante) {
        this.nomeReservante = nomeReservante;
    }

    public Poltrona getPoltrona() {
        return poltrona;
    }

    public void setPoltrona(Poltrona poltrona) {
        this.poltrona = poltrona;
    }

    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
    }
}
