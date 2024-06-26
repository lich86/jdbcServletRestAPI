package chervonnaya.servlet;

import chervonnaya.dao.CopyDAO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.service.CopyServiceImpl;
import chervonnaya.service.mappers.CopyMapper;
import chervonnaya.util.ConnectionManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet("/copy/*")
public class CopyServlet extends HttpServlet {
    private CopyServiceImpl copyService;
    private ObjectMapper objectMapper;
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        dataSource = ConnectionManager.getDataSource();
        this.copyService = new CopyServiceImpl(new CopyDAO(dataSource), Copy.class, Mappers.getMapper(CopyMapper.class));

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            Set<CopyDTO> copies = copyService.getAll();
            out.print(objectMapper.writeValueAsString(copies));
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                long id = Long.parseLong(splits[1]);
                CopyDTO copyDTO = copyService.getById(id).orElse(null);
                if (copyDTO != null) {
                    out.print(objectMapper.writeValueAsString(copyDTO));
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            CopyDTO copyDTO = objectMapper.readValue(request.getReader(), CopyDTO.class);
            Long id = copyService.save(copyDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("Copy created with ID: " + id);
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
            CopyDTO actualDTO = copyService.getById(id).orElse(null);
            if (actualDTO != null) {
                CopyDTO receivedDTO = objectMapper.readValue(request.getReader(), CopyDTO.class);
                receivedDTO.setId(id);
                copyService.update(id, receivedDTO);
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
            CopyDTO actualDTO = copyService.getById(id).orElse(null);
            if (actualDTO != null) {
                copyService.delete(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
