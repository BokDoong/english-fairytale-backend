package hanium.englishfairytale.tale.application;

import hanium.englishfairytale.common.files.FileManageService;
import hanium.englishfairytale.exception.NotFoundException;
import hanium.englishfairytale.exception.code.ErrorCode;
import hanium.englishfairytale.member.domain.Member;
import hanium.englishfairytale.member.domain.MemberRepository;
import hanium.englishfairytale.tale.application.dto.TaleCreateCommand;
import hanium.englishfairytale.tale.application.dto.TaleUpdateCommand;
import hanium.englishfairytale.tale.domain.*;
import hanium.englishfairytale.tale.application.dto.TaleCreateResponse;
import hanium.englishfairytale.tale.domain.factory.CreatedTale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaleCommandService {

    private final MemberRepository memberRepository;
    private final TaleRepository taleRepository;
    private final ImageRepository imageRepository;
    private final TaleManageService taleManageService;
    private final FileManageService fileManageService;

    @Transactional
    public TaleCreateResponse create(TaleCreateCommand taleCreateCommand) {
        Tale tale = createTale(taleCreateCommand);
        List<Keyword> keywords = findAndCreateKeywords(taleCreateCommand);
        return saveTaleAndKeywords(tale, keywords, taleCreateCommand.getImage());
    }

    @Transactional
    public void deleteTale(Long taleId) {
        findTale(taleId);
        taleRepository.deleteByTaleId(taleId);
    }

    @Transactional
    public void updateTaleImage(TaleUpdateCommand taleUpdateCommand) {
        Tale tale = findTale(taleUpdateCommand.getTaleId());
        tale.updateTaleImage(createAndSaveTaleImage(taleUpdateCommand.getImage()));
    }

    @Transactional
    public void deleteTaleImage(Long taleId, String imageStatus) {
        Tale tale = findTale(taleId);
        Long imageId = findTaleImageId(tale);
        deleteTaleImage(imageId);
        tale.putBasicImage(imageStatus);
    }

    private void deleteTaleImage(Long imageId) {
        imageRepository.delete(imageId);
    }

    private Long findTaleImageId(Tale tale) {
        verifyTaleImageIsEmpty(tale);
        return tale.getImageId();
    }

    private void verifyTaleImageIsEmpty(Tale tale) {
        tale.checkImageEmpty();
    }

    private Tale findTale(Long taleId) {
        return taleRepository.findTaleByTaleId(taleId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TALE_NOT_FOUND));
    }

    private Tale createTale(TaleCreateCommand taleCreateCommand) {
        Member member = findMember(taleCreateCommand.getMemberId());
        CreatedTale createdTale = createTaleByLLM(taleCreateCommand);
        return Tale.builder()
                .title(createdTale.getTitle())
                .engTale(createdTale.getEngTale())
                .korTale(createdTale.getKorTale())
                .member(member)
                .imageStatus(taleCreateCommand.getImageStatus())
                .build();
    }

    private Member findMember(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private CreatedTale createTaleByLLM(TaleCreateCommand taleCreateCommand) {
        verifyInputKeywords(taleCreateCommand);
        return taleManageService.post(taleCreateCommand.getModel(), taleCreateCommand.getKeywords());
    }

    private void verifyInputKeywords(TaleCreateCommand taleCreateCommand) {
        Keyword.verifyNumberOfKeywords(taleCreateCommand.getKeywords());
        Keyword.verifyDuplicatedKeywords(taleCreateCommand.getKeywords());
    }

    private TaleCreateResponse saveTaleAndKeywords(Tale tale, List<Keyword> keywords, MultipartFile image) {
        if (image != null) {
            tale.putImage(createAndSaveTaleImage(image));
        }
        saveTaleKeywords(tale, keywords);
        return new TaleCreateResponse(tale,keywords);
    }

    private void saveTaleKeywords(Tale tale, List<Keyword> keywords) {
        for(Keyword keyword: keywords) {
            taleRepository.save(createTaleKeyword(tale, keyword));
        }
    }

    private TaleKeyword createTaleKeyword(Tale tale, Keyword keyword) {
        return TaleKeyword.builder()
                .keyword(keyword)
                .tale(tale)
                .build();
    }

    private TaleImage createAndSaveTaleImage(MultipartFile image) {
        return new TaleImage(fileManageService.uploadImage(image));
    }

    private List<Keyword> findAndCreateKeywords(TaleCreateCommand taleCreateCommand) {
        List<Keyword> keywords = new ArrayList<>();

        taleCreateCommand.getKeywords().forEach(word -> {
            Optional<Keyword> optionalKeyword = taleRepository.findByWord(word);
            if (optionalKeyword.isEmpty()) {
                keywords.add(Keyword.builder().word(word).build());
            } else {
                keywords.add(optionalKeyword.get());
            }
        });

        return keywords;
    }
}
