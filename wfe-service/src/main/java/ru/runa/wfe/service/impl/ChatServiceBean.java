package ru.runa.wfe.service.impl;

import java.io.IOException;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import ru.runa.wfe.chat.ChatMessage;
import ru.runa.wfe.chat.ChatMessageException;
import ru.runa.wfe.chat.dto.ChatMessageFileDto;
import ru.runa.wfe.chat.dto.broadcast.MessageAddedBroadcast;
import ru.runa.wfe.chat.dto.request.AddMessageRequest;
import ru.runa.wfe.chat.dto.request.DeleteMessageRequest;
import ru.runa.wfe.chat.dto.request.EditMessageRequest;
import ru.runa.wfe.chat.logic.ChatFileLogic;
import ru.runa.wfe.chat.logic.ChatLogic;
import ru.runa.wfe.chat.socket.AddNewMessageHandler;
import ru.runa.wfe.chat.socket.DeleteMessageHandler;
import ru.runa.wfe.chat.socket.EditMessageHandler;
import ru.runa.wfe.service.decl.ChatServiceLocal;
import ru.runa.wfe.service.decl.ChatServiceRemote;
import ru.runa.wfe.service.interceptors.EjbExceptionSupport;
import ru.runa.wfe.service.interceptors.EjbTransactionSupport;
import ru.runa.wfe.service.interceptors.PerformanceObserver;
import ru.runa.wfe.user.User;

@Stateless(name = "ChatServiceBean")
@TransactionManagement(TransactionManagementType.BEAN)
@Interceptors({EjbExceptionSupport.class, PerformanceObserver.class, EjbTransactionSupport.class, SpringBeanAutowiringInterceptor.class})
@WebService(name = "ChatAPI", serviceName = "ChatWebService")
@SOAPBinding
public class ChatServiceBean implements ChatServiceLocal, ChatServiceRemote {

    @Autowired
    private ChatLogic chatLogic;
    @Autowired
    private ChatFileLogic chatFileLogic;
    @Autowired
    private AddNewMessageHandler addNewMessageHandler;
    @Autowired
    private EditMessageHandler editMessageHandler;
    @Autowired
    private DeleteMessageHandler deleteMessageHandler;

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public List<Long> getActiveChatIds(@WebParam(name = "user") @NonNull User user) {
        return chatLogic.getActiveChatIds(user);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public void saveMessage(@WebParam(name = "user") @NonNull User user, @WebParam(name = "message") AddMessageRequest request) {
        try {
            addNewMessageHandler.handleMessage(null, request, user);
        } catch (IOException exception) {
            throw new ChatMessageException("The message was not saved. Process ID: " + request.getProcessId());
        }
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public ChatMessage getMessage(@WebParam(name = "user") @NonNull User user, @WebParam(name = "messageId") Long id) {
        return chatLogic.getMessageById(user, id);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public List<MessageAddedBroadcast> getMessages(@WebParam(name = "user") @NonNull User user, @WebParam(name = "processId") Long processId,
            @WebParam(name = "firstIndex") Long firstIndex, @WebParam(name = "count") int count) {
        return chatLogic.getMessages(user, processId, firstIndex, count);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public List<MessageAddedBroadcast> getNewChatMessages(@WebParam(name = "user") @NonNull User user, @WebParam(name = "processId") Long processId) {
        return chatLogic.getNewMessages(user, processId);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public Long getNewChatMessagesCount(@WebParam(name = "user") @NonNull User user, @WebParam(name = "processId") Long processId) {
        return chatLogic.getNewMessagesCount(user, processId);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public List<Long> getNewMessagesCounts(@WebParam(name = "user") @NonNull User user,
            @WebParam(name = "processIds") List<Long> processIds) {
        return chatLogic.getNewMessagesCounts(user, processIds);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public Long getLastMessage(@WebParam(name = "user") @NonNull User user, @WebParam(name = "processId") Long processId) {
        return chatLogic.getLastMessage(user, processId);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public Long getLastReadMessage(@WebParam(name = "user") @NonNull User user, @WebParam(name = "processId") Long processId) {
        return chatLogic.getLastReadMessage(user, processId);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public void updateMessage(@WebParam(name = "user") @NonNull User user, @WebParam(name = "request") EditMessageRequest request) {
        try {
            editMessageHandler.handleMessage(null, request, user);
        } catch (IOException exception) {
            throw new ChatMessageException("The message was not updated. Message ID: " + request.getEditMessageId());
        }
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public void deleteMessage(@WebParam(name = "user") @NonNull User user, @WebParam(name = "request") DeleteMessageRequest request) {
        try {
            deleteMessageHandler.handleMessage(null, request, user);
        } catch (IOException exception) {
            throw new ChatMessageException("The message was not deleted. Message ID: " + request.getMessageId());
        }
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public void markMessageAsRead(@WebParam(name = "user") @NonNull User user, @WebParam(name = "messageId") Long id) {
        chatLogic.readMessage(user, id);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public ChatMessageFileDto getChatMessageFile(@WebParam(name = "user") @NonNull User user, @WebParam(name = "fileId") Long fileId) {
        return chatFileLogic.getById(user, fileId);
    }

    @WebMethod(exclude = false)
    @Override
    @WebResult(name = "result")
    public List<ChatMessageFileDto> getChatMessageFiles(@WebParam(name = "user") @NonNull User user,
            @WebParam(name = "message") ChatMessage message) {
        return chatFileLogic.getDtosByMessage(user, message);
    }

//    @WebMethod(exclude = false)
//    @Override
//    @WebResult(name = "result")
//    public void deleteFile(@WebParam(name = "user") @NonNull User user, @WebParam(name = "id") @NonNull Long id) {
//        chatFileLogic.deleteById(user, id);
//
//    }
}
