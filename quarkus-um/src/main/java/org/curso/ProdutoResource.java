package org.curso;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @POST
    @Transactional
    @Path("/salvar")
    public void salvaProduto(ProdutoDTO produto){
        Produto p = new Produto();
        p.nome = produto.nome;
        p.valor = produto.valor;
        p.persist();
    }

    @GET
    @Path("/listAll")
    public List<Produto> produtoListAll() {
        return Produto.listAll();
    }

    @PUT
    @Path("atualiza/{id}")
    @Transactional
    public void produtoAtualisa(@PathParam("id") Long id, ProdutoDTO dto) {
        Optional<Produto> produtoOp = Produto.findByIdOptional(id);
        if (produtoOp.isPresent()){
           Produto produto = produtoOp.get();
            produto.nome = dto.nome;
            produto.valor = dto.valor;
            produto.persist();
        }else {
            throw new NotFoundException();
        }
    }
    @PUT
    @Path("deleta/{id}")
    @Transactional
    public void produtoDelete(@PathParam("id") Long id, ProdutoDTO dto) {
        Optional<Produto> produtoOp = Produto.findByIdOptional(id);
      produtoOp.ifPresentOrElse(Produto::delete, ()->{
          throw new NotFoundException();
      });
    }
}