package cl.veterinary;

import cl.veterinary.model.Rol;
import cl.veterinary.service.RolService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.Scalars;
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

        // Tipo GraphQL para Rol
        GraphQLObjectType rolType = GraphQLObjectType.newObject()
                .name("Rol")
                .field(f -> f.name("id").type(Scalars.GraphQLID))
                .field(f -> f.name("nombre").type(Scalars.GraphQLString))
                .field(f -> f.name("descripcion").type(Scalars.GraphQLString))
                .build();

        // DataFetcher para obtener todos los roles
        DataFetcher<List<Rol>> rolesDataFetcher = env -> rolService.findRolAll();

        // Nuevo DataFetcher para obtener un Rol por ID
        DataFetcher<Rol> rolByIdDataFetcher = env -> {
            Long id = Long.parseLong(env.getArgument("id"));
            return rolService.finRolById(id).orElse(null);
        };

        // Definición del schema con query "getAllRoles"
        GraphQLObjectType queryType = GraphQLObjectType.newObject()
                .name("Query")
                .field(f -> f
                        .name("getAllRoles")
                        .type(GraphQLList.list(rolType))
                        .dataFetcher(rolesDataFetcher))
                .field(f -> f
                        .name("getRolById")
                        .type(rolType)
                        .argument(arg -> arg
                                .name("id")
                                .type(Scalars.GraphQLID))
                        .dataFetcher(rolByIdDataFetcher))
                .build();

        // Crear el schema
        GraphQLSchema schema = GraphQLSchema.newSchema()
                .query(queryType)
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
