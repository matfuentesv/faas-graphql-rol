package cl.veterinary;

import cl.veterinary.model.Rol;
import cl.veterinary.service.RolService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.*;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

public class FunctionGraphQL {

    private static final GraphQL graphQL;

    static {
        final ApplicationContext context =
                new SpringApplicationBuilder(SpringAzureApp.class).run();

        final RolService rolService =
                context.getBean(RolService.class);

        GraphQLObjectType rolType = GraphQLObjectType.newObject()
                .name("Rol")
                .field(f -> f.name("id").type(Scalars.GraphQLID))
                .field(f -> f.name("nombre").type(Scalars.GraphQLString))
                .field(f -> f.name("descripcion").type(Scalars.GraphQLString))
                .build();

        GraphQLInputObjectType rolInput = GraphQLInputObjectType.newInputObject()
                .name("RolInput")
                .field(f -> f.name("id").type(Scalars.GraphQLID)) // para update
                .field(f -> f.name("nombre").type(Scalars.GraphQLString))
                .field(f -> f.name("descripcion").type(Scalars.GraphQLString))
                .build();

        DataFetcher<List<Rol>> rolesDataFetcher = env -> rolService.findRolAll();

        DataFetcher<Rol> rolByIdDataFetcher = env -> {
            Long id = Long.parseLong(env.getArgument("id"));
            return rolService.finRolById(id).orElse(null);
        };

        DataFetcher<Rol> saveRolFetcher = env -> {
            Map<String, Object> input = env.getArgument("input");
            Rol rol = new Rol();
            rol.setNombre((String) input.get("nombre"));
            rol.setDescripcion((String) input.get("descripcion"));
            return rolService.saveRol(rol);
        };

        DataFetcher<Rol> updateRolFetcher = env -> {
            Map<String, Object> input = env.getArgument("input");
            Rol rol = new Rol();
            rol.setId(Long.parseLong((String) input.get("id")));
            rol.setNombre((String) input.get("nombre"));
            rol.setDescripcion((String) input.get("descripcion"));
            return rolService.updateRol(rol);
        };

        DataFetcher<String> deleteRolFetcher = env -> {
            Long id = Long.parseLong(env.getArgument("id"));
            rolService.deleteRol(id);
            return "Rol eliminado con ID: " + id;
        };

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
                .name("Query")
                .field(f -> f
                        .name("getAllRoles")
                        .type(GraphQLList.list(rolType))
                        .dataFetcher(rolesDataFetcher))
                .field(f -> f
                        .name("getRolById")
                        .type(rolType)
                        .argument(arg -> arg.name("id").type(Scalars.GraphQLID))
                        .dataFetcher(rolByIdDataFetcher))
                .build();

        GraphQLObjectType mutationType = GraphQLObjectType.newObject()
                .name("Mutation")
                .field(f -> f
                        .name("saveRol")
                        .type(rolType)
                        .argument(arg -> arg.name("input").type(rolInput))
                        .dataFetcher(saveRolFetcher))
                .field(f -> f
                        .name("updateRol")
                        .type(rolType)
                        .argument(arg -> arg.name("input").type(rolInput))
                        .dataFetcher(updateRolFetcher))
                .field(f -> f
                        .name("deleteRol")
                        .type(Scalars.GraphQLString)
                        .argument(arg -> arg.name("id").type(Scalars.GraphQLID))
                        .dataFetcher(deleteRolFetcher))
                .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
                .query(queryType)
                .mutation(mutationType)
                .build();

        graphQL = GraphQL.newGraphQL(schema).build();
    }

    @FunctionName("graphql")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Map<String, Object>> request,
            final ExecutionContext context) {

        String query = (String) request.getBody().get("query");

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .build();

        Map<String, Object> result = graphQL.execute(executionInput).toSpecification();

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(result)
                .build();
    }
}
