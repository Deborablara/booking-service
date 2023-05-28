package br.ufsm.poli.csi.webserver.Reserva;

public class Poltrona {
  private int id;
  private boolean reservada;
  private String classeCSS;

  public Poltrona(int id) {
    this.id = id;
    this.reservada = false;
    this.classeCSS = "poltrona";
  }

  public String getPoltronaHTML(Reserva reserva) {
    String classeCSS = this.reservada ? "poltronaReservada" : "poltrona";
    String nomeReservante = reserva != null ? reserva.getNomeReservante() : "";
    String data = reserva != null ? reserva.getDataReserva() : "";

    return "<a onclick='openPopup(" + id + ", " + reservada + ", \"" + nomeReservante + "\", \"" + data + "\")'>" +
        "<div class='center " + classeCSS + "'>" +
        "<p>" + id + "</p>" +
        "</div>" +
        "</a>";
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isReservada() {
    return reservada;
  }

  public void setReservada(boolean reservada) {
    this.reservada = reservada;
  }

  public String getClasseCSS() {
    return classeCSS;
  }

  public void setClasseCSS(String classeCSS) {
    this.classeCSS = classeCSS;
  }
}
