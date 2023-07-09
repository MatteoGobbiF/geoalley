package com.mgf.geoalley.service;

import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.exceptions.ReportNotFoundException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.ReportPhoto;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.PhotoRepository;
import com.mgf.geoalley.repository.ReportPhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportPhotoServiceImplTest {

    @Mock
    private ReportPhotoRepository mockReportPhotoRepository;
    @Mock
    private PhotoRepository mockPhotoRepository;

    private ReportPhotoServiceImpl reportPhotoServiceImplUnderTest;

    @BeforeEach
    public void setUp() {
        reportPhotoServiceImplUnderTest = new ReportPhotoServiceImpl(mockReportPhotoRepository, mockPhotoRepository);
    }

    @Test
    public void testFindPhotoById() throws Exception {

    	final Optional<Photo> photo = Optional.of(
                new Photo("url", "description", new User("username", "email", "password")));
        when(mockPhotoRepository.findById(0)).thenReturn(photo);

        final Photo result = reportPhotoServiceImplUnderTest.findPhotoById(0);

    }

    @Test
    public void testFindPhotoById_PhotoRepositoryReturnsAbsent() {

    	when(mockPhotoRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportPhotoServiceImplUnderTest.findPhotoById(0))
                .isInstanceOf(PhotoNotFoundException.class);
    }

    @Test
    public void testGetOpenReports() {

    	final List<ReportPhoto> reportPhotos = List.of(new ReportPhoto("description",
                new Photo("url", "description", new User("username", "email", "password"))));
        when(mockReportPhotoRepository.findByOpenTrue()).thenReturn(reportPhotos);

        final List<ReportPhoto> result = reportPhotoServiceImplUnderTest.getOpenReports();

    }

    @Test
    public void testGetOpenReports_ReportPhotoRepositoryReturnsNoItems() {

    	when(mockReportPhotoRepository.findByOpenTrue()).thenReturn(Collections.emptyList());

        final List<ReportPhoto> result = reportPhotoServiceImplUnderTest.getOpenReports();

        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testGetAllReports() {

    	final List<ReportPhoto> reportPhotos = List.of(new ReportPhoto("description",
                new Photo("url", "description", new User("username", "email", "password"))));
        when(mockReportPhotoRepository.findAll()).thenReturn(reportPhotos);

        final List<ReportPhoto> result = reportPhotoServiceImplUnderTest.getAllReports();

    }

    @Test
    public void testGetAllReports_ReportPhotoRepositoryReturnsNoItems() {

    	when(mockReportPhotoRepository.findAll()).thenReturn(Collections.emptyList());

        final List<ReportPhoto> result = reportPhotoServiceImplUnderTest.getAllReports();

        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testCloseReport() throws Exception {

    	final Optional<ReportPhoto> reportPhoto = Optional.of(new ReportPhoto("description",
                new Photo("url", "description", new User("username", "email", "password"))));
        when(mockReportPhotoRepository.findById(0)).thenReturn(reportPhoto);

        final ReportPhoto reportPhoto1 = new ReportPhoto("description",
                new Photo("url", "description", new User("username", "email", "password")));
        when(mockReportPhotoRepository.save(any(ReportPhoto.class))).thenReturn(reportPhoto1);

        reportPhotoServiceImplUnderTest.closeReport(0);

        verify(mockReportPhotoRepository).save(any(ReportPhoto.class));
    }

    @Test
    public void testCloseReport_ReportPhotoRepositoryFindByIdReturnsAbsent() {

    	when(mockReportPhotoRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportPhotoServiceImplUnderTest.closeReport(0))
                .isInstanceOf(ReportNotFoundException.class);
    }

    @Test
    public void testCloseReport_ReportPhotoRepositorySaveThrowsException() {

    	final Optional<ReportPhoto> reportPhoto = Optional.of(new ReportPhoto("description",
                new Photo("url", "description", new User("username", "email", "password"))));
        when(mockReportPhotoRepository.findById(0)).thenReturn(reportPhoto);

        when(mockReportPhotoRepository.save(any(ReportPhoto.class))).thenThrow(OptimisticLockingFailureException.class);

        assertThatThrownBy(() -> reportPhotoServiceImplUnderTest.closeReport(0))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}
