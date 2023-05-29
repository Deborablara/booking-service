package br.ufsm.poli.csi.webserver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import br.ufsm.poli.csi.webserver.Reserva.Poltrona;
import br.ufsm.poli.csi.webserver.Reserva.Reserva;

public class Server {

    private static List<Poltrona> poltronas;
    private static List<Reserva> reservas;

    private static class RequestHandler extends Thread {
        private Socket socket;

        public RequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                // Lógica de tratamento da requisição
                byte[] buffer = new byte[1024];
                int nBytes = in.read(buffer);
                if (nBytes == -1) {
                    socket.close();
                    return;
                }
                String str = new String(buffer, 0, nBytes);
                String[] linhas = str.split("\n");
                String[] linha1 = linhas[0].split(" ");
                String recurso = linha1[1];

                if (recurso.equals("/")) {
                    recurso = "/index.html";
                } else if (recurso.contains("/compraingresso")) { // formulário
                    Reserva reserva = extrairParametrosGet("http://localhost:8080" + recurso);

                    // Exibindo a reserva
                    if (reserva != null) {
                        String enderecoIP = socket.getInetAddress().getHostAddress();
                        reserva.setIp(enderecoIP);

                        String log = "Nome do reservante: " + reserva.getNomeReservante() + "\n"
                                + "ID da Poltrona: " + reserva.getPoltrona().getId() + "\n" +
                                "Data da Reserva: " + reserva.getDataReserva() + "\n" +
                                "Endereço de IP: " + reserva.getIp() + "\n\n\n";

                        synchronized (reservas) {
                            reservas.add(reserva);
                        }

                        FileOutputStream logs = new FileOutputStream("log.txt", true);
                        logs.write(log.getBytes(StandardCharsets.UTF_8));
                        logs.flush();
                        logs.close();
                    }
                    recurso = "/index.html";
                }

                recurso = recurso.replace('/', File.separatorChar);

                String header = "HTTP/1.1 200 OK\n" +
                        "Content-Type: " + getContentType(recurso) + "; charset=utf-8\n\n";
                File f = new File("arquivos_html" + recurso);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                if (!f.exists()) {
                    bout.write("404 NOT FOUND\n\n".getBytes(StandardCharsets.UTF_8));
                } else {
                    InputStream fileIn = new FileInputStream(f);
                    bout.write(header.getBytes(StandardCharsets.UTF_8));
                    // escreve arquivo
                    nBytes = fileIn.read(buffer);
                    do {
                        if (nBytes > 0) {
                            bout.write(buffer, 0, nBytes);
                            nBytes = fileIn.read(buffer);
                        }
                    } while (nBytes == 1024);
                    if (nBytes > 0) {
                        bout.write(buffer, 0, nBytes);
                    }
                }
                if (getContentType(recurso).equals("text/html")) {
                    String saida = processaVariaveis(bout);
                    out.write(saida.getBytes(StandardCharsets.UTF_8));
                } else {
                    out.write(bout.toByteArray());
                }

                out.flush();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Poltrona> getPoltronas() {
        if (poltronas == null) {
            poltronas = new ArrayList<>();

            for (int i = 1; i < 385; i++) {
                Poltrona poltrona = new Poltrona(i);
                poltronas.add(poltrona);
            }
        }

        return poltronas;
    }

    private static String processaVariaveis(ByteArrayOutputStream bout) {
        List<String> poltronasHtml = new ArrayList<>();

        for (Poltrona poltrona : poltronas) {
            Reserva r = getReservaByPoltronaId(poltrona.getId());
            poltronasHtml.add(poltrona.getPoltronaHTML(r));
        }

        String str = new String(bout.toByteArray());
        String poltronasHtmlString = String.join("", poltronasHtml);
        str = str.replace("${poltronas}", poltronasHtmlString);
        return str;
    }

    private static String getContentType(String nomeRecurso) {

        if (nomeRecurso.toLowerCase().endsWith(".css")) {
            return "text/css";
        } else if (nomeRecurso.toLowerCase().endsWith(".jpg")
                || nomeRecurso.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (nomeRecurso.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (nomeRecurso.toLowerCase().contains(".js")) {
            return "application/javascript";
        } else {
            return "text/html";
        }
    }

    // extrai parametros da url
    public static Reserva extrairParametrosGet(String urlString) {
        Reserva reserva = null;

        try {
            URL url = new URL(urlString);
            String query = url.getQuery();
            String[] pares = query.split("&");

            String nomeReservante = null;
            int idPoltrona = 0;

            for (String par : pares) {
                String[] chaveValor = par.split("=");
                if (chaveValor.length == 2) {
                    String chave = URLDecoder.decode(chaveValor[0], "UTF-8");
                    String valor = URLDecoder.decode(chaveValor[1], "UTF-8");

                    if (chave.equals("nome")) {
                        nomeReservante = valor;
                    } else if (chave.equals("idPoltrona")) {
                        idPoltrona = Integer.valueOf(valor);
                    }
                }
            }

            Poltrona p = obterPoltronaPorId(idPoltrona);

            if (!p.isReservada()) {
                Date dataAtual = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                String dataReserva = sdf.format(dataAtual);
                reserva = new Reserva(nomeReservante, p, dataReserva);
                p.setReservada(true);
            }

        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return reserva;
    }

    // lista poltronas disponíveis
    private static List<Poltrona> getPoltronasDisponiveis() {
        List<Poltrona> poltronasDisponiveis = new ArrayList<>();

        for (Poltrona poltrona : poltronas) {
            if (!poltrona.isReservada()) {
                poltronasDisponiveis.add(poltrona);
            }
        }

        return poltronasDisponiveis;
    }

    // obtem poltrona por id
    private static Poltrona obterPoltronaPorId(int idPoltrona) {
        List<Poltrona> poltronasDisponveis = getPoltronasDisponiveis();

        for (Poltrona poltrona : poltronasDisponveis) {
            if (poltrona.getId() == idPoltrona) {
                return poltrona;
            }
        }

        return null; // Retorna null se a poltrona não for encontrada
    }

    public static Reserva getReservaByPoltronaId(int id) {
        if (reservas == null) {
            reservas = new ArrayList<>();
        }

        Optional<Reserva> optionalReserva = reservas.stream()
                .filter(reserva -> reserva.getPoltrona().getId() == id && reserva.getPoltrona().isReservada())
                .findFirst();

        return optionalReserva.orElse(null);
    }

    public static void main(String[] args) throws IOException {
        poltronas = getPoltronas();
        reservas = new ArrayList<>();

        ServerSocket ss = new ServerSocket(8080);
        System.out.println("Iniciando server socket...");

        while (true) {
            Socket socket = ss.accept();
            System.out.println("Conexão recebida");

            // Cria uma nova instância de RequestHandler e inicia a thread
            RequestHandler requestHandler = new RequestHandler(socket);
            requestHandler.start();
        }
    }

}
