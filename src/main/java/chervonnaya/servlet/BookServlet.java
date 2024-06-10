package chervonnaya.servlet;

import chervonnaya.dao.BookDAO;
import chervonnaya.dto.BookDTO;
import chervonnaya.model.Book;
import chervonnaya.service.BookServiceImpl;
import chervonnaya.service.mappers.BookMapper;
import chervonnaya.util.ConnectionManager;
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
import java.sql.SQLException;
import java.util.Set;

@WebServlet("/book/*")
public class BookServlet extends HttpServlet {
    private BookServiceImpl bookService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.bookService = new BookServiceImpl(new BookDAO(ConnectionManager.getDataSource()), Book.class, Mappers.getMapper(BookMapper.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            Set<BookDTO> books = bookService.getAll();
            out.print(objectMapper.writeValueAsString(books));
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                long id = Long.parseLong(splits[1]);
                BookDTO bookDTO = bookService.getById(id).orElse(null);
                if (bookDTO != null) {
                    out.print(objectMapper.writeValueAsString(bookDTO));
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
        PrintWriter out = response.getWriter();
        try {
            BookDTO bookDTO = objectMapper.readValue(request.getReader(), BookDTO.class);
            Long id = bookService.save(bookDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.write("Book created with ID: " + id);
        } catch (JsonParseException | JsonMappingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            long id = Long.parseLong(pathInfo.split("/")[1]);
            BookDTO actualDTO = bookService.getById(id).orElse(null);
            if (actualDTO != null) {
                BookDTO receivedDTO = objectMapper.readValue(request.getReader(), BookDTO.class);
                receivedDTO.setId(id);
                bookService.update(id, receivedDTO);
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
            BookDTO actualDTO = bookService.getById(id).orElse(null);
            if (actualDTO != null) {
                bookService.delete(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
