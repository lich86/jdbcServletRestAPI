package chervonnaya.servlet;

import chervonnaya.dao.CopyDAO;
import chervonnaya.dto.CopyDTO;
import chervonnaya.model.Copy;
import chervonnaya.service.CopyService;
import chervonnaya.service.mappers.CopyMapper;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet("/copy/*")
public class CopyServlet extends HttpServlet {
    private CopyService copyService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.copyService = new CopyService(new CopyDAO(), Copy.class, Mappers.getMapper(CopyMapper.class));
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
            Set<CopyDTO> persons = copyService.getAll();
            out.print(objectMapper.writeValueAsString(persons));
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
        try {
            CopyDTO copyDTO = objectMapper.readValue(request.getReader(), CopyDTO.class);
            copyService.save(copyDTO);
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
            CopyDTO actualDTO = copyService.getById(id).orElse(null);
            if (actualDTO != null) {
                CopyDTO receivedDTO = objectMapper.readValue(request.getReader(), CopyDTO.class);
                receivedDTO.setCopyId(id);
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
