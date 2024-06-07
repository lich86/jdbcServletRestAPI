package chervonnaya.servlet;

import chervonnaya.dao.AuthorDAO;
import chervonnaya.dto.AuthorDTO;
import chervonnaya.model.Author;
import chervonnaya.service.AuthorServiceImpl;
import chervonnaya.service.mappers.AuthorMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet("/author/*")
public class AuthorServlet extends HttpServlet {
    private AuthorServiceImpl authorService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authorService = new AuthorServiceImpl(new AuthorDAO(), Author.class, Mappers.getMapper(AuthorMapper.class));
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            Set<AuthorDTO> persons = authorService.getAll();
            out.print(objectMapper.writeValueAsString(persons));
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                long id = Long.parseLong(splits[1]);
                AuthorDTO authorDTO = authorService.getById(id).orElse(null);
                if (authorDTO != null) {
                    out.print(objectMapper.writeValueAsString(authorDTO));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
        try {
            AuthorDTO authorDTO = objectMapper.readValue(request.getReader(), AuthorDTO.class);
            authorService.save(authorDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (JsonParseException | JsonMappingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid JSON format\"}");
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Error reading request\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            long id = Long.parseLong(pathInfo.split("/")[1]);
            AuthorDTO actualDTO = authorService.getById(id).orElse(null);
            if (actualDTO != null) {
                AuthorDTO receivedDTO = objectMapper.readValue(request.getReader(), AuthorDTO.class);
                receivedDTO.setId(id);
                authorService.update(id, receivedDTO);
                response.setContentType("application/json");
                out.print(objectMapper.writeValueAsString(receivedDTO));
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            long id = Long.parseLong(pathInfo.split("/")[1]);
            AuthorDTO actualDTO = authorService.getById(id).orElse(null);
            if (actualDTO != null) {
                authorService.delete(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
