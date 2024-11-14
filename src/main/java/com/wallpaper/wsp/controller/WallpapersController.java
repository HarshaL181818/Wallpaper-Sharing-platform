package com.wallpaper.wsp.controller;

import com.wallpaper.wsp.model.Wallpaper;
import com.wallpaper.wsp.service.WallpaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallpapers")
@CrossOrigin(origins = "***")
//add the ip f=of frontend host and port number
public class WallpapersController {

    @Autowired
    private WallpaperService wallpaperService;

    @GetMapping
    public ResponseEntity<List<Wallpaper>> getAllWallpapers() {
        List<Wallpaper> wallpapers = wallpaperService.getAllWallpapers();
        return ResponseEntity.ok(wallpapers);
    }
}
