# booking-service - este projeto não está finalizado ainda
 O código apresentado implementa um servidor HTTP básico, sem o uso de servidores HTTP existentes, como Tomcat ou Apache. O servidor é desenvolvido a partir do zero, utilizando as funcionalidades disponíveis na linguagem Java para lidar com sockets, entrada/saída, manipulação de arquivos e processamento de texto.
 
 **REQUISITOS**
 - Funcionalidades:

    - Página inicial para visualização dos lugares vagos e reservados, exibindo o nome do responsável pela reserva e a data/hora da reserva.
    - Endereço para que um usuário possa reservar um lugar livre, utilizando o método GET e passando os parâmetros nome e local desejado. Após a reserva, o usuário será redirecionado para a página inicial com uma mensagem de sucesso ou erro relacionada à reserva.

- Características de implementação:

    - Implementação própria do servidor HTTP, sem utilização de servidores HTTP existentes, como Tomcat ou Apache.
    - Registro do endereço IP de origem da requisição e dos dados da reserva para posterior emissão de bilhetes. Implementação do problema dos produtores/consumidores para escrita do log.
    - Utilização de múltiplas threads, onde cada requisição ao servidor será tratada em uma thread exclusiva.
    - Prevenção de reserva duplicada de um mesmo lugar por mais de uma pessoa.
    - A organização do código e a organização visual da página serão avaliadas e corresponderão a 15% da nota.
    - Não é necessário implementar persistência de dados, com exceção do arquivo de logs.
