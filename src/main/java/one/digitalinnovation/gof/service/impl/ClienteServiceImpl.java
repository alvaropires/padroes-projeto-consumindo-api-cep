package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.repository.ClienteRepository;
import one.digitalinnovation.gof.repository.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link @AutoWired}. Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * @author alvaro
 */


@Service
public class ClienteServiceImpl implements ClienteService{
//    Singleton: Injetar os componentes do Spring com @Autowired

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ViaCepService viaCepService;

//    Strategy: Implementar os métodos definidos na interface.
//    Facade: Abstrair integrações com subsistemas, provendo uma interface simples.


    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
//        FIXME Buscar Cliente por ID, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()){
            salvarClienteComCep(cliente);
        }

    }

    @Override
    public void deletar(Long id) {
//        FIXME Deletar Cliente por ID.
        clienteRepository.deleteById(id);

    }

    private void salvarClienteComCep(Cliente cliente) {
        //        FIXME Verificar se o Endereço do Cliente já existe (pelo Cep)

        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
//        FIXME Caso não exista, integrar com o ViaCep e persistir o retorno

            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
//        FIXME Inserir Cliente, vinculando o Endereço (novo ou existente).

        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }
}
