package com.wallpaper.wsp.service;

import com.wallpaper.wsp.model.Wallpaper;
import com.wallpaper.wsp.repo.WallpaperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WallpaperService {

    @Autowired
    private WallpaperRepo wallpaperRepository; // Assume you have a JPA repository

    public Wallpaper addWallpaper(Wallpaper wallpaper) {
        return wallpaperRepository.save(wallpaper);
    }
    public List<Wallpaper> getAllWallpapers() {
        return wallpaperRepository.findAll();
    }
}
